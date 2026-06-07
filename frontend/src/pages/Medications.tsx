import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { medicationApi } from '@/lib/pharmacyApi';
import type { Medication, MedicationRequestDto } from '@/lib/pharmacyApi';
import { patientApi } from '@/lib/patientApi';
import { prescriptionApi } from '@/lib/pharmacyApi';
import type { PrescriptionCreateDto, PrescriptionResponseDto } from '@/lib/pharmacyApi';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from '@/components/ui/dialog';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from '@/components/ui/select';
import { useForm, Controller, useFieldArray } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { format, parseISO } from 'date-fns';
import {
  Plus, Pill, Pencil, Trash2, Loader2, AlertCircle,
  Search, FileText, X, User, ClipboardList,
} from 'lucide-react';

type Tab = 'medications' | 'prescriptions';

const medSchema = z.object({
  code: z.string().min(1, 'Mã thuốc là bắt buộc').max(50),
  name: z.string().min(1, 'Tên thuốc là bắt buộc').max(255),
  activeIngredient: z.string().max(255).optional().or(z.literal('')),
  unit: z.string().max(50).optional().or(z.literal('')),
});

function MedicationModal({
  open, onClose, medication,
}: {
  open: boolean;
  onClose: () => void;
  medication?: Medication | null;
}) {
  const qc = useQueryClient();
  const isEdit = !!medication;

  const { register, handleSubmit, reset, formState: { errors } } = useForm({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(medSchema) as any,
    defaultValues: medication ?? { code: '', name: '', activeIngredient: '', unit: '' },
  });

  const mutation = useMutation({
    mutationFn: (dto: MedicationRequestDto) =>
      isEdit ? medicationApi.update(medication!.id, dto) : medicationApi.create(dto),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['medications'] });
      reset();
      onClose();
    },
  });

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onSubmit = (data: any) => mutation.mutate(data);

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>{isEdit ? 'Chỉnh sửa thuốc' : 'Thêm thuốc mới'}</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-2">
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-sm font-medium">Mã thuốc <span className="text-destructive">*</span></label>
              <Input placeholder="VD: PAR500" {...register('code')} />
              {errors.code && <p className="text-xs text-destructive">{errors.code.message as string}</p>}
            </div>
            <div className="space-y-1">
              <label className="text-sm font-medium">Đơn vị</label>
              <Input placeholder="VD: viên, ml, gói" {...register('unit')} />
            </div>
          </div>
          <div className="space-y-1">
            <label className="text-sm font-medium">Tên thuốc <span className="text-destructive">*</span></label>
            <Input placeholder="VD: Paracetamol 500mg" {...register('name')} />
            {errors.name && <p className="text-xs text-destructive">{errors.name.message as string}</p>}
          </div>
          <div className="space-y-1">
            <label className="text-sm font-medium">Hoạt chất</label>
            <Input placeholder="VD: Paracetamol" {...register('activeIngredient')} />
          </div>
          {mutation.isError && (
            <div className="flex items-center gap-2 text-sm text-destructive bg-destructive/10 p-3 rounded-lg">
              <AlertCircle size={16} /> Đã xảy ra lỗi. Vui lòng thử lại.
            </div>
          )}
          <DialogFooter>
            <Button type="button" variant="outline" onClick={onClose}>Hủy</Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending && <Loader2 size={16} className="mr-2 animate-spin" />}
              {isEdit ? 'Lưu' : 'Thêm thuốc'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function MedicationsTab() {
  const [search, setSearch] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [selected, setSelected] = useState<Medication | null>(null);
  const qc = useQueryClient();

  const { data: medications = [], isLoading } = useQuery({
    queryKey: ['medications'],
    queryFn: medicationApi.getAll,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => medicationApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['medications'] }),
  });

  const filtered = medications.filter(
    (m) =>
      m.name.toLowerCase().includes(search.toLowerCase()) ||
      m.code.toLowerCase().includes(search.toLowerCase()) ||
      m.activeIngredient?.toLowerCase().includes(search.toLowerCase())
  );

  const openEdit = (m: Medication) => { setSelected(m); setModalOpen(true); };
  const openCreate = () => { setSelected(null); setModalOpen(true); };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div className="relative max-w-sm flex-1">
          <Search size={15} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
          <Input
            className="pl-9"
            placeholder="Tìm theo tên, mã, hoạt chất..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
        <Button onClick={openCreate} className="gap-2">
          <Plus size={16} /> Thêm thuốc
        </Button>
      </div>

      <div className="rounded-xl border bg-card shadow-sm overflow-hidden">
        {isLoading ? (
          <div className="flex items-center justify-center py-20 gap-3 text-muted-foreground">
            <Loader2 size={20} className="animate-spin" /> Đang tải...
          </div>
        ) : filtered.length === 0 ? (
          <div className="flex flex-col items-center justify-center py-20 text-muted-foreground gap-2">
            <Pill size={40} className="opacity-20" />
            <p>{search ? 'Không tìm thấy kết quả' : 'Chưa có thuốc nào'}</p>
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b bg-muted/40">
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Mã thuốc</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Tên thuốc</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Hoạt chất</th>
                <th className="text-left px-4 py-3 font-medium text-muted-foreground">Đơn vị</th>
                <th className="text-right px-4 py-3"></th>
              </tr>
            </thead>
            <tbody>
              {filtered.map((m) => (
                <tr key={m.id} className="border-b last:border-0 hover:bg-muted/30 transition-colors">
                  <td className="px-4 py-3">
                    <span className="font-mono text-xs bg-muted px-2 py-1 rounded text-muted-foreground">{m.code}</span>
                  </td>
                  <td className="px-4 py-3 font-medium">{m.name}</td>
                  <td className="px-4 py-3 text-muted-foreground">{m.activeIngredient || '—'}</td>
                  <td className="px-4 py-3 text-muted-foreground">{m.unit || '—'}</td>
                  <td className="px-4 py-3">
                    <div className="flex items-center justify-end gap-1">
                      <Button size="sm" variant="ghost" className="h-8 w-8 p-0 text-muted-foreground hover:text-foreground" onClick={() => openEdit(m)}>
                        <Pencil size={14} />
                      </Button>
                      <Button size="sm" variant="ghost" className="h-8 w-8 p-0 text-muted-foreground hover:text-destructive" onClick={() => deleteMutation.mutate(m.id)}>
                        <Trash2 size={14} />
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      <MedicationModal open={modalOpen} onClose={() => setModalOpen(false)} medication={selected} />
    </div>
  );
}

const presSchema = z.object({
  patientId: z.coerce.number().min(1, 'Chọn bệnh nhân'),
  doctorName: z.string().optional(),
  startDate: z.string().min(1, 'Ngày bắt đầu là bắt buộc'),
  endDate: z.string().min(1, 'Ngày kết thúc là bắt buộc'),
  diagnosis: z.string().optional(),
  items: z.array(z.object({
    medicationId: z.coerce.number().min(1),
    dosage: z.string().optional(),
    quantity: z.coerce.number().min(1, 'Số lượng tối thiểu là 1'),
  })).min(1, 'Phải có ít nhất 1 loại thuốc'),
});

function PrescriptionModal({ open, onClose }: { open: boolean; onClose: () => void }) {
  const qc = useQueryClient();
  const { data: patients } = useQuery({ queryKey: ['patients-all'], queryFn: () => patientApi.getAll(0, 100), enabled: open });
  const { data: medications = [] } = useQuery({ queryKey: ['medications'], queryFn: medicationApi.getAll, enabled: open });

  const { register, handleSubmit, control, watch, formState: { errors } } = useForm({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(presSchema) as any,
    defaultValues: {
      patientId: 0,
      doctorName: '',
      startDate: '',
      endDate: '',
      diagnosis: '',
      items: [{ medicationId: 0, dosage: '', quantity: 1 }],
    },
  });

  const { fields, append, remove } = useFieldArray({ control, name: 'items' });

  const mutation = useMutation({
    mutationFn: (dto: PrescriptionCreateDto) => prescriptionApi.create(dto),
    onSuccess: (_, vars) => {
      qc.invalidateQueries({ queryKey: ['prescriptions', vars.patientId] });
      onClose();
    },
  });

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onSubmit = (data: any) => {
    mutation.mutate(data as PrescriptionCreateDto);
  };

  const selectedPatientId = watch('patientId');

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Tạo đơn thuốc</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5 py-2">
          {/* Bệnh nhân & Bác sĩ */}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1">
              <label className="text-sm font-medium">Bệnh nhân <span className="text-destructive">*</span></label>
              <Controller
                control={control}
                name="patientId"
                render={({ field }) => (
                  <Select onValueChange={(v) => field.onChange(Number(v))} value={String(field.value || '')}>
                    <SelectTrigger>
                      <SelectValue placeholder="Chọn bệnh nhân..." />
                    </SelectTrigger>
                    <SelectContent>
                      {patients?.data.map((p) => (
                        <SelectItem key={p.id} value={String(p.id)}>{p.fullName} — {p.patientCode}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                )}
              />
              {errors.patientId && <p className="text-xs text-destructive">{errors.patientId.message as string}</p>}
            </div>
            <div className="space-y-1">
              <label className="text-sm font-medium">Bác sĩ kê đơn</label>
              <Input placeholder="BS. Nguyễn Văn A" {...register('doctorName')} />
            </div>
          </div>

          {/* Ngày bắt đầu & Kết thúc */}
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1">
              <label className="text-sm font-medium">Ngày bắt đầu <span className="text-destructive">*</span></label>
              <Input type="date" {...register('startDate')} />
              {errors.startDate && <p className="text-xs text-destructive">{errors.startDate.message as string}</p>}
            </div>
            <div className="space-y-1">
              <label className="text-sm font-medium">Ngày kết thúc <span className="text-destructive">*</span></label>
              <Input type="date" {...register('endDate')} />
              {errors.endDate && <p className="text-xs text-destructive">{errors.endDate.message as string}</p>}
            </div>
          </div>

          {/* Chẩn đoán */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Chẩn đoán</label>
            <Input placeholder="VD: Viêm họng cấp tính" {...register('diagnosis')} />
          </div>

          {/* Danh sách thuốc */}
          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <label className="text-sm font-medium">Thuốc trong đơn <span className="text-destructive">*</span></label>
              <Button
                type="button"
                variant="outline"
                size="sm"
                className="gap-1.5 h-8"
                onClick={() => append({ medicationId: 0, dosage: '', quantity: 1 })}
              >
                <Plus size={13} /> Thêm thuốc
              </Button>
            </div>

            <div className="space-y-2">
              {fields.map((field, index) => (
                <div key={field.id} className="flex gap-2 items-start p-3 rounded-xl bg-muted/40 border">
                  {/* Thuốc */}
                  <div className="flex-1 min-w-0 space-y-1">
                    <label className="text-xs text-muted-foreground">Thuốc</label>
                    <Controller
                      control={control}
                      name={`items.${index}.medicationId`}
                      render={({ field: f }) => (
                        <Select onValueChange={(v) => f.onChange(Number(v))} value={String(f.value || '')}>
                          <SelectTrigger className="h-8 text-sm">
                            <SelectValue placeholder="Chọn thuốc..." />
                          </SelectTrigger>
                          <SelectContent>
                            {medications.map((m) => (
                              <SelectItem key={m.id} value={String(m.id)}>
                                {m.name} ({m.code})
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      )}
                    />
                  </div>
                  {/* Liều dùng */}
                  <div className="w-32 space-y-1">
                    <label className="text-xs text-muted-foreground">Liều dùng</label>
                    <Input className="h-8 text-sm" placeholder="1 viên/ngày" {...register(`items.${index}.dosage`)} />
                  </div>
                  {/* Số lượng */}
                  <div className="w-20 space-y-1">
                    <label className="text-xs text-muted-foreground">Số lượng</label>
                    <Input className="h-8 text-sm" type="number" min="1" placeholder="30" {...register(`items.${index}.quantity`)} />
                  </div>
                  {/* Remove */}
                  <Button
                    type="button"
                    variant="ghost"
                    size="sm"
                    className="h-8 w-8 p-0 mt-5 text-muted-foreground hover:text-destructive shrink-0"
                    onClick={() => remove(index)}
                    disabled={fields.length === 1}
                  >
                    <X size={14} />
                  </Button>
                </div>
              ))}
            </div>
            {errors.items && <p className="text-xs text-destructive">Phải có ít nhất 1 loại thuốc</p>}
          </div>

          {mutation.isError && (
            <div className="flex items-center gap-2 text-sm text-destructive bg-destructive/10 p-3 rounded-lg">
              <AlertCircle size={16} /> Đã xảy ra lỗi. Vui lòng thử lại.
            </div>
          )}

          <DialogFooter>
            <Button type="button" variant="outline" onClick={onClose}>Hủy</Button>
            <Button type="submit" disabled={mutation.isPending || !selectedPatientId}>
              {mutation.isPending && <Loader2 size={16} className="mr-2 animate-spin" />}
              Tạo đơn thuốc
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function PrescriptionCard({ p, onDelete }: { p: PrescriptionResponseDto; onDelete: () => void }) {
  const [expanded, setExpanded] = useState(false);
  return (
    <div className="bg-card border rounded-xl shadow-sm overflow-hidden">
      <div
        className="flex items-center justify-between px-4 py-3 cursor-pointer hover:bg-muted/30 transition-colors"
        onClick={() => setExpanded((v) => !v)}
      >
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
            <FileText size={16} className="text-primary" />
          </div>
          <div>
            <p className="font-medium text-sm">
              Đơn ngày {format(parseISO(p.startDate), 'dd/MM/yyyy')} — {format(parseISO(p.endDate), 'dd/MM/yyyy')}
            </p>
            <p className="text-xs text-muted-foreground mt-0.5">
              {p.diagnosis || 'Không có chẩn đoán'} · {p.doctorName || 'Không rõ bác sĩ'}
            </p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <span className="text-xs bg-primary/10 text-primary px-2 py-0.5 rounded-full font-medium">
            {p.items?.length ?? 0} loại thuốc
          </span>
          <Button size="sm" variant="ghost" className="h-7 w-7 p-0 text-muted-foreground hover:text-destructive" onClick={(e) => { e.stopPropagation(); onDelete(); }}>
            <Trash2 size={13} />
          </Button>
        </div>
      </div>
      {expanded && p.items?.length > 0 && (
        <div className="border-t px-4 py-3 bg-muted/20">
          <table className="w-full text-xs">
            <thead>
              <tr className="text-muted-foreground">
                <th className="text-left py-1 font-medium">Tên thuốc</th>
                <th className="text-center py-1 font-medium">Liều dùng</th>
                <th className="text-center py-1 font-medium">Số lượng</th>
              </tr>
            </thead>
            <tbody>
              {p.items.map((item, i) => (
                <tr key={i} className="border-t border-border/50">
                  <td className="py-1.5">{item.medicationName || `ID: ${item.medicationId}`}</td>
                  <td className="py-1.5 text-center text-muted-foreground">{item.dosage || '—'}</td>
                  <td className="py-1.5 text-center font-medium">{item.quantity}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

function PrescriptionsTab() {
  const [selectedPatientId, setSelectedPatientId] = useState<number | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const qc = useQueryClient();

  const { data: patients } = useQuery({ queryKey: ['patients-all'], queryFn: () => patientApi.getAll(0, 100) });
  const { data: prescriptions = [], isLoading } = useQuery({
    queryKey: ['prescriptions', selectedPatientId],
    queryFn: () => prescriptionApi.getByPatient(selectedPatientId!),
    enabled: !!selectedPatientId,
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => prescriptionApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['prescriptions', selectedPatientId] }),
  });

  return (
    <div className="flex gap-6">
      {/* Patient Sidebar */}
      <div className="w-64 shrink-0 space-y-1">
        <p className="text-xs font-medium text-muted-foreground uppercase tracking-wider mb-2 px-1">Bệnh nhân</p>
        {patients?.data.map((p) => (
          <button
            key={p.id}
            onClick={() => setSelectedPatientId(p.id)}
            className={`w-full text-left px-3 py-2.5 rounded-xl border text-sm transition-all ${
              selectedPatientId === p.id
                ? 'bg-primary text-primary-foreground border-primary shadow-sm'
                : 'bg-card hover:bg-muted border-border'
            }`}
          >
            <p className="font-medium truncate">{p.fullName}</p>
            <p className={`text-xs mt-0.5 ${selectedPatientId === p.id ? 'text-primary-foreground/70' : 'text-muted-foreground'}`}>
              {p.patientCode}
            </p>
          </button>
        ))}
      </div>

      {/* Prescriptions Panel */}
      <div className="flex-1 min-w-0">
        {selectedPatientId ? (
          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium text-muted-foreground">
                {prescriptions.length} đơn thuốc
              </p>
              <Button size="sm" onClick={() => setModalOpen(true)} className="gap-1.5">
                <Plus size={14} /> Tạo đơn thuốc
              </Button>
            </div>
            {isLoading ? (
              <div className="flex justify-center py-12"><Loader2 size={20} className="animate-spin text-muted-foreground" /></div>
            ) : prescriptions.length === 0 ? (
              <div className="flex flex-col items-center justify-center py-16 bg-card border rounded-xl text-muted-foreground gap-2">
                <ClipboardList size={40} className="opacity-20" />
                <p className="text-sm">Bệnh nhân chưa có đơn thuốc nào</p>
              </div>
            ) : (
              prescriptions.map((p) => (
                <PrescriptionCard key={p.id} p={p} onDelete={() => deleteMutation.mutate(p.id)} />
              ))
            )}
          </div>
        ) : (
          <div className="flex flex-col items-center justify-center h-64 text-muted-foreground gap-3 bg-card border rounded-xl">
            <User size={48} className="opacity-20" />
            <p className="font-medium">Chọn một bệnh nhân</p>
            <p className="text-sm">để xem lịch sử đơn thuốc</p>
          </div>
        )}
      </div>

      <PrescriptionModal open={modalOpen} onClose={() => setModalOpen(false)} />
    </div>
  );
}

export function Medications() {
  const [tab, setTab] = useState<Tab>('medications');

  return (
    <div className="flex flex-col gap-6 animate-in fade-in-0">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Thuốc & Kê đơn</h1>
        <p className="text-muted-foreground text-sm mt-0.5">Quản lý danh mục thuốc và đơn thuốc bệnh nhân</p>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 p-1 bg-muted/50 rounded-xl w-fit border">
        {(['medications', 'prescriptions'] as Tab[]).map((t) => (
          <button
            key={t}
            onClick={() => setTab(t)}
            className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-all ${
              tab === t
                ? 'bg-background text-foreground shadow-sm'
                : 'text-muted-foreground hover:text-foreground'
            }`}
          >
            {t === 'medications' ? <><Pill size={15} /> Danh mục thuốc</> : <><ClipboardList size={15} /> Đơn thuốc</>}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      {tab === 'medications' ? <MedicationsTab /> : <PrescriptionsTab />}
    </div>
  );
}
