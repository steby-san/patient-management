import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { patientApi } from '@/lib/patientApi';
import type { Patient, PatientCreateDto } from '@/lib/patientApi';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Search,
  Plus,
  Pencil,
  Trash2,
  ChevronLeft,
  ChevronRight,
  User,
  Phone,
  MapPin,
  Calendar,
  Loader2,
  AlertCircle,
} from 'lucide-react';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

// ─── Validation Schema ───────────────────────────────────────────────────────
const patientSchema = z.object({
  fullName: z.string().min(2, 'Tên phải có ít nhất 2 ký tự').max(100),
  dateOfBirth: z.string().min(1, 'Ngày sinh là bắt buộc'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']).refine((v) => v, { message: 'Chọn giới tính' }),
  phoneNumber: z.string().regex(/^(0[3-9]\d{8})$/, 'Số điện thoại không hợp lệ'),
  email: z.string().email('Email không hợp lệ').optional().or(z.literal('')),
  address: z.string().max(500).optional().or(z.literal('')),
});
type PatientFormValues = z.infer<typeof patientSchema>;

// ─── Helpers ─────────────────────────────────────────────────────────────────
const formatDate = (date: string) =>
  new Date(date).toLocaleDateString('vi-VN');

const genderLabel: Record<string, string> = {
  MALE: 'Nam',
  FEMALE: 'Nữ',
  OTHER: 'Khác',
};

const genderBadge: Record<string, string> = {
  MALE: 'bg-blue-50 text-blue-700 ring-blue-200',
  FEMALE: 'bg-pink-50 text-pink-700 ring-pink-200',
  OTHER: 'bg-gray-100 text-gray-600 ring-gray-200',
};

// ─── Patient Form Modal ───────────────────────────────────────────────────────
function PatientModal({
  open,
  onClose,
  patient,
}: {
  open: boolean;
  onClose: () => void;
  patient?: Patient | null;
}) {
  const qc = useQueryClient();
  const isEdit = !!patient;

  const {
    register,
    handleSubmit,
    control,
    reset,
    formState: { errors, isSubmitting },
  } = useForm<PatientFormValues>({
    resolver: zodResolver(patientSchema),
    defaultValues: {
      fullName: '',
      dateOfBirth: '',
      gender: undefined,
      phoneNumber: '',
      email: '',
      address: '',
    },
  });

  useEffect(() => {
    if (patient) {
      reset({
        fullName: patient.fullName,
        dateOfBirth: patient.dateOfBirth?.slice(0, 10) ?? '',
        gender: patient.gender,
        phoneNumber: patient.phoneNumber ?? '',
        email: patient.email ?? '',
        address: patient.address ?? '',
      });
    } else {
      reset({ fullName: '', dateOfBirth: '', gender: undefined, phoneNumber: '', email: '', address: '' });
    }
  }, [patient, reset]);

  const mutation = useMutation({
    mutationFn: (dto: PatientCreateDto) =>
      isEdit ? patientApi.update(patient!.id, dto) : patientApi.create(dto),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['patients'] });
      onClose();
    },
  });

  const onSubmit = (data: PatientFormValues) => {
    mutation.mutate(data as PatientCreateDto);
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle className="text-lg font-semibold">
            {isEdit ? 'Chỉnh sửa bệnh nhân' : 'Thêm bệnh nhân mới'}
          </DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 py-2">
          {/* Họ tên */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Họ và tên <span className="text-destructive">*</span></label>
            <Input placeholder="Nguyễn Văn An" {...register('fullName')} />
            {errors.fullName && <p className="text-xs text-destructive">{errors.fullName.message}</p>}
          </div>

          <div className="grid grid-cols-2 gap-4">
            {/* Ngày sinh */}
            <div className="space-y-1">
              <label className="text-sm font-medium">Ngày sinh <span className="text-destructive">*</span></label>
              <Input type="date" {...register('dateOfBirth')} />
              {errors.dateOfBirth && <p className="text-xs text-destructive">{errors.dateOfBirth.message}</p>}
            </div>

            {/* Giới tính */}
            <div className="space-y-1">
              <label className="text-sm font-medium">Giới tính <span className="text-destructive">*</span></label>
              <Controller
                control={control}
                name="gender"
                render={({ field }) => (
                  <Select onValueChange={field.onChange} value={field.value}>
                    <SelectTrigger>
                      <SelectValue placeholder="Chọn..." />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="MALE">Nam</SelectItem>
                      <SelectItem value="FEMALE">Nữ</SelectItem>
                      <SelectItem value="OTHER">Khác</SelectItem>
                    </SelectContent>
                  </Select>
                )}
              />
              {errors.gender && <p className="text-xs text-destructive">{errors.gender.message}</p>}
            </div>
          </div>

          {/* Số điện thoại */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Số điện thoại <span className="text-destructive">*</span></label>
            <Input placeholder="0901234567" {...register('phoneNumber')} />
            {errors.phoneNumber && <p className="text-xs text-destructive">{errors.phoneNumber.message}</p>}
          </div>

          {/* Email */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Email</label>
            <Input type="email" placeholder="example@email.com" {...register('email')} />
            {errors.email && <p className="text-xs text-destructive">{errors.email.message}</p>}
          </div>

          {/* Địa chỉ */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Địa chỉ</label>
            <Input placeholder="Quận 1, TP HCM" {...register('address')} />
            {errors.address && <p className="text-xs text-destructive">{errors.address.message}</p>}
          </div>

          {mutation.isError && (
            <div className="flex items-center gap-2 text-sm text-destructive bg-destructive/10 p-3 rounded-lg">
              <AlertCircle size={16} />
              <span>Đã xảy ra lỗi. Vui lòng thử lại.</span>
            </div>
          )}

          <DialogFooter className="pt-2">
            <Button type="button" variant="outline" onClick={onClose}>Hủy</Button>
            <Button type="submit" disabled={isSubmitting || mutation.isPending}>
              {mutation.isPending && <Loader2 size={16} className="mr-2 animate-spin" />}
              {isEdit ? 'Lưu thay đổi' : 'Tạo mới'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

// ─── Delete Confirm Modal ─────────────────────────────────────────────────────
function DeleteModal({
  open,
  onClose,
  patient,
}: {
  open: boolean;
  onClose: () => void;
  patient?: Patient | null;
}) {
  const qc = useQueryClient();
  const mutation = useMutation({
    mutationFn: () => patientApi.delete(patient!.id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['patients'] });
      onClose();
    },
  });

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-sm">
        <DialogHeader>
          <DialogTitle>Xác nhận xóa</DialogTitle>
        </DialogHeader>
        <p className="text-sm text-muted-foreground py-2">
          Bạn có chắc muốn xóa bệnh nhân{' '}
          <span className="font-semibold text-foreground">{patient?.fullName}</span>? Hành động này không thể hoàn tác.
        </p>
        <DialogFooter>
          <Button variant="outline" onClick={onClose}>Hủy</Button>
          <Button
            variant="destructive"
            onClick={() => mutation.mutate()}
            disabled={mutation.isPending}
          >
            {mutation.isPending && <Loader2 size={16} className="mr-2 animate-spin" />}
            Xóa bệnh nhân
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

// ─── Main Patients Page ───────────────────────────────────────────────────────
export function Patients() {
  const [page, setPage] = useState(0);
  const [search, setSearch] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [selected, setSelected] = useState<Patient | null>(null);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['patients', page, search],
    queryFn: () => patientApi.getAll(page, 10, search),
    placeholderData: (prev) => prev,
  });

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setSearch(searchInput);
    setPage(0);
  };

  const openCreate = () => { setSelected(null); setModalOpen(true); };
  const openEdit = (p: Patient) => { setSelected(p); setModalOpen(true); };
  const openDelete = (p: Patient) => { setSelected(p); setDeleteOpen(true); };

  return (
    <div className="flex flex-col gap-6 animate-in fade-in-0">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Quản lý bệnh nhân</h1>
          <p className="text-muted-foreground text-sm mt-0.5">
            {data ? `${data.totalElements} bệnh nhân trong hệ thống` : 'Đang tải...'}
          </p>
        </div>
        <Button onClick={openCreate} className="gap-2">
          <Plus size={16} />
          Thêm bệnh nhân
        </Button>
      </div>

      {/* Search bar */}
      <form onSubmit={handleSearch} className="flex gap-2 max-w-md">
        <div className="relative flex-1">
          <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
          <Input
            className="pl-9"
            placeholder="Tìm theo tên, mã BN, số điện thoại..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />
        </div>
        <Button type="submit" variant="outline">Tìm</Button>
      </form>

      {/* Table */}
      <div className="rounded-xl border bg-card shadow-sm overflow-hidden">
        {isLoading ? (
          <div className="flex items-center justify-center py-24 gap-3 text-muted-foreground">
            <Loader2 size={20} className="animate-spin" />
            <span>Đang tải dữ liệu...</span>
          </div>
        ) : isError ? (
          <div className="flex flex-col items-center justify-center py-24 gap-3 text-muted-foreground">
            <AlertCircle size={40} className="text-destructive/50" />
            <p className="font-medium">Không thể tải dữ liệu</p>
            <p className="text-sm">Kiểm tra kết nối đến Backend API.</p>
          </div>
        ) : (
          <>
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b bg-muted/40">
                  <th className="text-left px-4 py-3 font-medium text-muted-foreground">Mã BN</th>
                  <th className="text-left px-4 py-3 font-medium text-muted-foreground">Họ và tên</th>
                  <th className="text-left px-4 py-3 font-medium text-muted-foreground">Giới tính</th>
                  <th className="text-left px-4 py-3 font-medium text-muted-foreground">Ngày sinh</th>
                  <th className="text-left px-4 py-3 font-medium text-muted-foreground">Số điện thoại</th>
                  <th className="text-left px-4 py-3 font-medium text-muted-foreground">Địa chỉ</th>
                  <th className="text-right px-4 py-3 font-medium text-muted-foreground">Hành động</th>
                </tr>
              </thead>
              <tbody>
                {data?.data.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="text-center py-16 text-muted-foreground">
                      <User size={40} className="mx-auto mb-3 opacity-30" />
                      <p>Không tìm thấy bệnh nhân nào</p>
                    </td>
                  </tr>
                ) : (
                  data?.data.map((patient) => (
                    <tr
                      key={patient.id}
                      className="border-b last:border-0 hover:bg-muted/30 transition-colors"
                    >
                      <td className="px-4 py-3">
                        <span className="font-mono text-xs bg-muted px-2 py-1 rounded text-muted-foreground">
                          {patient.patientCode}
                        </span>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center gap-2">
                          <div className="w-8 h-8 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
                            <User size={14} className="text-primary" />
                          </div>
                          <span className="font-medium">{patient.fullName}</span>
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <span className={`text-xs font-medium px-2 py-0.5 rounded-full ring-1 ${genderBadge[patient.gender] ?? genderBadge.OTHER}`}>
                          {genderLabel[patient.gender] ?? patient.gender}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-muted-foreground">
                        <div className="flex items-center gap-1.5">
                          <Calendar size={13} />
                          {patient.dateOfBirth ? formatDate(patient.dateOfBirth) : '—'}
                        </div>
                      </td>
                      <td className="px-4 py-3 text-muted-foreground">
                        <div className="flex items-center gap-1.5">
                          <Phone size={13} />
                          {patient.phoneNumber || '—'}
                        </div>
                      </td>
                      <td className="px-4 py-3 text-muted-foreground max-w-[200px] truncate">
                        <div className="flex items-center gap-1.5">
                          <MapPin size={13} className="shrink-0" />
                          {patient.address || '—'}
                        </div>
                      </td>
                      <td className="px-4 py-3">
                        <div className="flex items-center justify-end gap-1">
                          <Button
                            size="sm"
                            variant="ghost"
                            className="h-8 w-8 p-0 text-muted-foreground hover:text-foreground"
                            onClick={() => openEdit(patient)}
                          >
                            <Pencil size={14} />
                          </Button>
                          <Button
                            size="sm"
                            variant="ghost"
                            className="h-8 w-8 p-0 text-muted-foreground hover:text-destructive"
                            onClick={() => openDelete(patient)}
                          >
                            <Trash2 size={14} />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>

            {/* Pagination */}
            {(data?.totalPages ?? 0) > 1 && (
              <div className="flex items-center justify-between px-4 py-3 border-t bg-muted/20 text-sm text-muted-foreground">
                <span>
                  Trang {(data?.page ?? 0) + 1} / {data?.totalPages}
                  {' · '}{data?.totalElements} bệnh nhân
                </span>
                <div className="flex items-center gap-1">
                  <Button
                    variant="outline"
                    size="sm"
                    className="h-8 w-8 p-0"
                    disabled={page === 0}
                    onClick={() => setPage((p) => p - 1)}
                  >
                    <ChevronLeft size={15} />
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    className="h-8 w-8 p-0"
                    disabled={page >= (data?.totalPages ?? 1) - 1}
                    onClick={() => setPage((p) => p + 1)}
                  >
                    <ChevronRight size={15} />
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </div>

      {/* Modals */}
      <PatientModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        patient={selected}
      />
      <DeleteModal
        open={deleteOpen}
        onClose={() => setDeleteOpen(false)}
        patient={selected}
      />
    </div>
  );
}
