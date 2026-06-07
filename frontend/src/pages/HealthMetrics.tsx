import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { format, parseISO } from 'date-fns';
import { healthMetricApi } from '@/lib/appointmentApi';
import type { HealthMetricInputDto } from '@/lib/appointmentApi';
import { patientApi } from '@/lib/patientApi';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter,
} from '@/components/ui/dialog';
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from '@/components/ui/select';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Plus, Activity, Heart, Weight, Ruler, Droplets,
  Loader2, AlertCircle, Trash2, TrendingUp, TrendingDown,
} from 'lucide-react';

// Validation
const schema = z.object({
  patientId: z.coerce.number().min(1, 'Chọn bệnh nhân'),
  weight: z.coerce.number().min(1).max(500).optional().or(z.literal('')),
  height: z.coerce.number().min(10).max(300).optional().or(z.literal('')),
  systolic: z.coerce.number().min(50).max(300).optional().or(z.literal('')),
  diastolic: z.coerce.number().min(30).max(200).optional().or(z.literal('')),
  heartRate: z.coerce.number().min(20).max(300).optional().or(z.literal('')),
}).refine(
  (d) => d.weight || d.height || d.systolic || d.diastolic || d.heartRate,
  { message: 'Phải nhập ít nhất một chỉ số sức khỏe', path: ['weight'] }
);
// eslint-disable-next-line @typescript-eslint/no-explicit-any
type FormValues = any;

// BMI Calculator
function calcBmi(weight?: number | null, height?: number | null) {
  if (!weight || !height || height === 0) return null;
  return (weight / ((height / 100) ** 2)).toFixed(1);
}

function BmiTag({ bmi }: { bmi: string | null }) {
  if (!bmi) return null;
  const v = parseFloat(bmi);
  const [label, cls] = v < 18.5
    ? ['Thiếu cân', 'bg-amber-50 text-amber-700']
    : v < 25
    ? ['Bình thường', 'bg-emerald-50 text-emerald-700']
    : v < 30
    ? ['Thừa cân', 'bg-orange-50 text-orange-700']
    : ['Béo phì', 'bg-red-50 text-red-700'];
  return (
    <span className={`text-xs font-medium px-2 py-0.5 rounded-full ${cls}`}>
      BMI {bmi} · {label}
    </span>
  );
}

// BP Status
function bpStatus(systolic?: number | null, diastolic?: number | null) {
  if (!systolic || !diastolic) return null;
  if (systolic >= 140 || diastolic >= 90)
    return { label: 'Cao', cls: 'text-red-600' };
  if (systolic >= 120 || diastolic >= 80)
    return { label: 'Bình thường cao', cls: 'text-amber-600' };
  return { label: 'Bình thường', cls: 'text-emerald-600' };
}

// Add Metric Modal 
function AddMetricModal({ open, onClose }: { open: boolean; onClose: () => void }) {
  const qc = useQueryClient();
  const { data: patients } = useQuery({
    queryKey: ['patients-all'],
    queryFn: () => patientApi.getAll(0, 100),
    enabled: open,
  });

  const { register, handleSubmit, control, watch, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { patientId: 0 },
  });

  const mutation = useMutation({
    mutationFn: (dto: HealthMetricInputDto) => healthMetricApi.add(dto),
    onSuccess: (_, vars) => {
      qc.invalidateQueries({ queryKey: ['metrics', vars.patientId] });
      reset();
      onClose();
    },
  });

  const onSubmit = (data: FormValues) => {
    const dto: HealthMetricInputDto = {
      patientId: data.patientId,
      weight: data.weight ? Number(data.weight) : undefined,
      height: data.height ? Number(data.height) : undefined,
      systolic: data.systolic ? Number(data.systolic) : undefined,
      diastolic: data.diastolic ? Number(data.diastolic) : undefined,
      heartRate: data.heartRate ? Number(data.heartRate) : undefined,
    };
    mutation.mutate(dto);
  };

  const selectedPatientId = watch('patientId');

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Ghi nhận chỉ số sức khỏe</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-2">
          {/* Bệnh nhân */}
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
                      <SelectItem key={p.id} value={String(p.id)}>
                        {p.fullName} — {p.patientCode}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
            {errors.patientId && <p className="text-xs text-destructive">{errors.patientId.message as string}</p>}
          </div>

          <p className="text-xs text-muted-foreground">Nhập ít nhất một chỉ số bên dưới:</p>

          {/* Cân nặng & Chiều cao */}
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-xs font-medium flex items-center gap-1"><Weight size={12} /> Cân nặng (kg)</label>
              <Input type="number" step="0.1" placeholder="65.0" {...register('weight')} />
            </div>
            <div className="space-y-1">
              <label className="text-xs font-medium flex items-center gap-1"><Ruler size={12} /> Chiều cao (cm)</label>
              <Input type="number" step="0.1" placeholder="170.0" {...register('height')} />
            </div>
          </div>

          {/* Huyết áp */}
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-1">
              <label className="text-xs font-medium flex items-center gap-1"><Droplets size={12} /> Tâm thu (mmHg)</label>
              <Input type="number" placeholder="120" {...register('systolic')} />
            </div>
            <div className="space-y-1">
              <label className="text-xs font-medium flex items-center gap-1"><Droplets size={12} /> Tâm trương (mmHg)</label>
              <Input type="number" placeholder="80" {...register('diastolic')} />
            </div>
          </div>

          {/* Nhịp tim */}
          <div className="space-y-1">
            <label className="text-xs font-medium flex items-center gap-1"><Heart size={12} /> Nhịp tim (bpm)</label>
            <Input type="number" placeholder="75" {...register('heartRate')} />
          </div>
          {errors.weight && (errors.weight.message as string)?.includes('ít nhất') && (
            <p className="text-xs text-destructive">{errors.weight.message as string}</p>
          )}
          {mutation.isError && (
            <div className="flex items-center gap-2 text-sm text-destructive bg-destructive/10 p-3 rounded-lg">
              <AlertCircle size={16} /> Đã xảy ra lỗi. Vui lòng thử lại.
            </div>
          )}

          <DialogFooter className="pt-2">
            <Button type="button" variant="outline" onClick={onClose}>Hủy</Button>
            <Button type="submit" disabled={mutation.isPending || !selectedPatientId}>
              {mutation.isPending && <Loader2 size={16} className="mr-2 animate-spin" />}
              Lưu chỉ số
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

// Patient Metrics View
function PatientMetricsView({ patientId }: { patientId: number }) {
  const qc = useQueryClient();
  const { data: metrics = [], isLoading } = useQuery({
    queryKey: ['metrics', patientId],
    queryFn: () => healthMetricApi.getByPatient(patientId),
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => healthMetricApi.delete(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['metrics', patientId] }),
  });

  if (isLoading) return <div className="flex justify-center py-8"><Loader2 size={20} className="animate-spin text-muted-foreground" /></div>;
  if (metrics.length === 0) return (
    <div className="text-center py-12 text-muted-foreground">
      <Activity size={36} className="mx-auto mb-2 opacity-30" />
      <p className="text-sm">Chưa có chỉ số nào được ghi nhận</p>
    </div>
  );

  const latest = metrics[0];
  const bmi = calcBmi(latest.weight ? Number(latest.weight) : null, latest.height ? Number(latest.height) : null);
  const bp = bpStatus(latest.systolic, latest.diastolic);

  return (
    <div className="space-y-3">
      {/* Latest summary cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 mb-4">
        {[
          { icon: <Weight size={16} />, label: 'Cân nặng', value: latest.weight ? `${latest.weight} kg` : '—' },
          { icon: <Ruler size={16} />, label: 'Chiều cao', value: latest.height ? `${latest.height} cm` : '—' },
          {
            icon: <Droplets size={16} />,
            label: 'Huyết áp',
            value: latest.systolic ? `${latest.systolic}/${latest.diastolic} mmHg` : '—',
            extra: bp ? <span className={`text-xs font-medium ${bp.cls}`}>{bp.label}</span> : undefined,
          },
          { icon: <Heart size={16} />, label: 'Nhịp tim', value: latest.heartRate ? `${latest.heartRate} bpm` : '—' },
        ].map(({ icon, label, value, extra }) => (
          <div key={label} className="bg-muted/40 rounded-xl p-3 border">
            <div className="flex items-center gap-1.5 text-muted-foreground mb-1 text-xs">{icon} {label}</div>
            <p className="font-semibold text-sm">{value}</p>
            {extra && <div className="mt-0.5">{extra}</div>}
          </div>
        ))}
      </div>

      {bmi && <div className="mb-3"><BmiTag bmi={bmi} /></div>}

      {/* History table */}
      <div className="rounded-xl border overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="border-b bg-muted/40">
              <th className="text-left px-3 py-2.5 text-xs font-medium text-muted-foreground">Ngày đo</th>
              <th className="text-center px-3 py-2.5 text-xs font-medium text-muted-foreground">CN (kg)</th>
              <th className="text-center px-3 py-2.5 text-xs font-medium text-muted-foreground">CC (cm)</th>
              <th className="text-center px-3 py-2.5 text-xs font-medium text-muted-foreground">HA (mmHg)</th>
              <th className="text-center px-3 py-2.5 text-xs font-medium text-muted-foreground">Nhịp tim</th>
              <th className="text-right px-3 py-2.5 text-xs font-medium text-muted-foreground"></th>
            </tr>
          </thead>
          <tbody>
            {metrics.map((m, i) => {
              const prev = metrics[i + 1];
              const weightDiff = prev?.weight && m.weight ? Number(m.weight) - Number(prev.weight) : null;
              return (
                <tr key={m.id} className="border-b last:border-0 hover:bg-muted/20 transition-colors">
                  <td className="px-3 py-2.5 text-muted-foreground text-xs">
                    {format(parseISO(m.measuredAt), 'dd/MM/yyyy HH:mm')}
                  </td>
                  <td className="px-3 py-2.5 text-center">
                    <div className="flex items-center justify-center gap-1">
                      {m.weight ?? '—'}
                      {weightDiff !== null && (
                        weightDiff > 0
                          ? <TrendingUp size={12} className="text-red-400" />
                          : <TrendingDown size={12} className="text-emerald-400" />
                      )}
                    </div>
                  </td>
                  <td className="px-3 py-2.5 text-center text-muted-foreground">{m.height ?? '—'}</td>
                  <td className="px-3 py-2.5 text-center">
                    {m.systolic ? (
                      <span className={m.systolic >= 140 ? 'text-red-600 font-medium' : ''}>
                        {m.systolic}/{m.diastolic}
                      </span>
                    ) : '—'}
                  </td>
                  <td className="px-3 py-2.5 text-center text-muted-foreground">{m.heartRate ?? '—'}</td>
                  <td className="px-3 py-2.5 text-right">
                    <Button
                      size="sm" variant="ghost"
                      className="h-7 w-7 p-0 text-muted-foreground hover:text-destructive"
                      onClick={() => deleteMutation.mutate(m.id)}
                    >
                      <Trash2 size={13} />
                    </Button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

// Health Metrics Page
export function HealthMetrics() {
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedPatientId, setSelectedPatientId] = useState<number | null>(null);

  const { data: patients } = useQuery({
    queryKey: ['patients-all'],
    queryFn: () => patientApi.getAll(0, 100),
  });

  return (
    <div className="flex flex-col gap-6 animate-in fade-in-0">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Chỉ số sức khỏe</h1>
          <p className="text-muted-foreground text-sm mt-0.5">Theo dõi và ghi nhận chỉ số theo từng bệnh nhân</p>
        </div>
        <Button onClick={() => setModalOpen(true)} className="gap-2">
          <Plus size={16} /> Ghi nhận chỉ số
        </Button>
      </div>

      <div className="flex gap-6">
        {/* Patient list sidebar */}
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

        {/* Metrics panel */}
        <div className="flex-1 min-w-0 bg-card border rounded-xl p-5 shadow-sm">
          {selectedPatientId ? (
            <PatientMetricsView patientId={selectedPatientId} />
          ) : (
            <div className="flex flex-col items-center justify-center h-64 text-muted-foreground gap-3">
              <Activity size={48} className="opacity-20" />
              <p className="font-medium">Chọn một bệnh nhân</p>
              <p className="text-sm">để xem lịch sử chỉ số sức khỏe</p>
            </div>
          )}
        </div>
      </div>

      <AddMetricModal open={modalOpen} onClose={() => setModalOpen(false)} />
    </div>
  );
}
