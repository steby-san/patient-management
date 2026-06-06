import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { format, addDays, subDays, parseISO, isToday } from 'date-fns';
import { vi } from 'date-fns/locale';
import { appointmentApi } from '@/lib/appointmentApi';
import type { Appointment, AppointmentCreateDto } from '@/lib/appointmentApi';
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
  ChevronLeft, ChevronRight, Plus, Calendar, Clock, User,
  Stethoscope, CheckCircle2, XCircle, AlertCircle, Loader2, Trash2,
} from 'lucide-react';

// Status Config
const STATUS_CONFIG: Record<string, { label: string; class: string; icon: React.ReactNode }> = {
  SCHEDULED: {
    label: 'Đã lên lịch',
    class: 'bg-blue-50 text-blue-700 ring-blue-200',
    icon: <Clock size={12} />,
  },
  COMPLETED: {
    label: 'Hoàn thành',
    class: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
    icon: <CheckCircle2 size={12} />,
  },
  CANCELLED: {
    label: 'Đã hủy',
    class: 'bg-red-50 text-red-700 ring-red-200',
    icon: <XCircle size={12} />,
  },
  PENDING: {
    label: 'Chờ xác nhận',
    class: 'bg-amber-50 text-amber-700 ring-amber-200',
    icon: <AlertCircle size={12} />,
  },
};

// Validation Schema 
const schema = z.object({
  patientId: z.coerce.number().min(1, 'Chọn bệnh nhân'),
  appointmentTime: z.string().min(1, 'Chọn thời gian hẹn'),
  reason: z.string().optional(),
  doctorName: z.string().optional(),
});

// Appointment Form Modal
function AppointmentModal({
  open, onClose, date,
}: {
  open: boolean;
  onClose: () => void;
  date: string;
}) {
  const qc = useQueryClient();
  const { data: patients } = useQuery({
    queryKey: ['patients-all'],
    queryFn: () => patientApi.getAll(0, 100),
    enabled: open,
  });

  const { register, handleSubmit, control, reset, formState: { errors } } = useForm({
    resolver: zodResolver(schema) as any,
    defaultValues: {
      patientId: 0,
      appointmentTime: `${date}T08:00`,
      reason: '',
      doctorName: '',
    },
  });

  const mutation = useMutation({
    mutationFn: (dto: AppointmentCreateDto) => appointmentApi.create(dto),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['appointments', date] });
      reset();
      onClose();
    },
  });

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const onSubmit = (data: any) => {
    mutation.mutate({
      ...data,
      appointmentTime: new Date(data.appointmentTime).toISOString(),
    });
  };

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Đặt lịch hẹn mới</DialogTitle>
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
            {errors.patientId && <p className="text-xs text-destructive">{errors.patientId.message}</p>}
          </div>

          {/* Thời gian */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Thời gian hẹn <span className="text-destructive">*</span></label>
            <Input type="datetime-local" {...register('appointmentTime')} />
            {errors.appointmentTime && <p className="text-xs text-destructive">{errors.appointmentTime.message}</p>}
          </div>

          {/* Lý do */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Lý do khám</label>
            <Input placeholder="Khám tổng quát, tái khám..." {...register('reason')} />
          </div>

          {/* Bác sĩ */}
          <div className="space-y-1">
            <label className="text-sm font-medium">Bác sĩ phụ trách</label>
            <Input placeholder="BS. Nguyễn Văn A" {...register('doctorName')} />
          </div>

          {mutation.isError && (
            <div className="flex items-center gap-2 text-sm text-destructive bg-destructive/10 p-3 rounded-lg">
              <AlertCircle size={16} /> Đã xảy ra lỗi. Vui lòng thử lại.
            </div>
          )}

          <DialogFooter className="pt-2">
            <Button type="button" variant="outline" onClick={onClose}>Hủy</Button>
            <Button type="submit" disabled={mutation.isPending}>
              {mutation.isPending && <Loader2 size={16} className="mr-2 animate-spin" />}
              Đặt lịch
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}

// Status Badge
function StatusBadge({ status }: { status: string }) {
  const cfg = STATUS_CONFIG[status] ?? STATUS_CONFIG.PENDING;
  return (
    <span className={`inline-flex items-center gap-1 text-xs font-medium px-2 py-0.5 rounded-full ring-1 ${cfg.class}`}>
      {cfg.icon} {cfg.label}
    </span>
  );
}

// Appointment Card
function AppointmentCard({ appt, onCancel, onComplete }: {
  appt: Appointment;
  onCancel: () => void;
  onComplete: () => void;
}) {
  const time = format(parseISO(appt.appointmentTime), 'HH:mm');
  return (
    <div className="bg-card rounded-xl border p-4 flex flex-col gap-3 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-start justify-between gap-2">
        <div className="flex items-center gap-2">
          <div className="w-9 h-9 rounded-full bg-primary/10 flex items-center justify-center shrink-0">
            <User size={16} className="text-primary" />
          </div>
          <div>
            <p className="font-semibold text-sm leading-tight">{appt.patientName}</p>
            <p className="text-xs text-muted-foreground flex items-center gap-1 mt-0.5">
              <Clock size={11} /> {time}
            </p>
          </div>
        </div>
        <StatusBadge status={appt.status} />
      </div>

      {appt.reason && (
        <p className="text-sm text-muted-foreground bg-muted/50 rounded-lg px-3 py-2 leading-relaxed">
          {appt.reason}
        </p>
      )}

      {appt.doctorName && (
        <div className="flex items-center gap-1.5 text-xs text-muted-foreground">
          <Stethoscope size={12} /> {appt.doctorName}
        </div>
      )}

      {appt.status === 'SCHEDULED' && (
        <div className="flex gap-2 pt-1 border-t">
          <Button size="sm" variant="outline" className="flex-1 h-8 text-xs text-emerald-600 hover:text-emerald-700 hover:bg-emerald-50" onClick={onComplete}>
            <CheckCircle2 size={13} className="mr-1" /> Hoàn thành
          </Button>
          <Button size="sm" variant="outline" className="h-8 w-8 p-0 text-muted-foreground hover:text-destructive hover:bg-red-50" onClick={onCancel}>
            <Trash2 size={13} />
          </Button>
        </div>
      )}
    </div>
  );
}

// Main Appointments Page
export function Appointments() {
  const [selectedDate, setSelectedDate] = useState(() => format(new Date(), 'yyyy-MM-dd'));
  const [modalOpen, setModalOpen] = useState(false);
  const qc = useQueryClient();

  const { data: appointments = [], isLoading } = useQuery({
    queryKey: ['appointments', selectedDate],
    queryFn: () => appointmentApi.getByDate(selectedDate),
  });

  const cancelMutation = useMutation({
    mutationFn: (id: number) => appointmentApi.cancel(id),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['appointments', selectedDate] }),
  });

  const completeMutation = useMutation({
    mutationFn: (id: number) => appointmentApi.updateStatus(id, 'COMPLETED'),
    onSuccess: () => qc.invalidateQueries({ queryKey: ['appointments', selectedDate] }),
  });

  const prevDay = () => setSelectedDate(format(subDays(parseISO(selectedDate), 1), 'yyyy-MM-dd'));
  const nextDay = () => setSelectedDate(format(addDays(parseISO(selectedDate), 1), 'yyyy-MM-dd'));
  const goToday = () => setSelectedDate(format(new Date(), 'yyyy-MM-dd'));

  const scheduled = appointments.filter((a) => a.status === 'SCHEDULED');
  const completed = appointments.filter((a) => a.status === 'COMPLETED');
  const cancelled = appointments.filter((a) => a.status === 'CANCELLED');

  const displayDate = format(parseISO(selectedDate), "EEEE, dd 'tháng' MM, yyyy", { locale: vi });

  return (
    <div className="flex flex-col gap-6 animate-in fade-in-0">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Lịch hẹn</h1>
          <p className="text-muted-foreground text-sm mt-0.5 capitalize">{displayDate}</p>
        </div>
        <Button onClick={() => setModalOpen(true)} className="gap-2">
          <Plus size={16} /> Đặt lịch hẹn
        </Button>
      </div>

      {/* Date navigator */}
      <div className="flex items-center gap-2">
        <Button variant="outline" size="sm" className="h-9 w-9 p-0" onClick={prevDay}>
          <ChevronLeft size={16} />
        </Button>
        <div className="flex items-center gap-2 bg-card border rounded-lg px-4 py-2 shadow-sm min-w-[200px] justify-center">
          <Calendar size={15} className="text-primary" />
          <span className="text-sm font-medium">
            {isToday(parseISO(selectedDate)) ? 'Hôm nay' : format(parseISO(selectedDate), 'dd/MM/yyyy')}
          </span>
        </div>
        <Button variant="outline" size="sm" className="h-9 w-9 p-0" onClick={nextDay}>
          <ChevronRight size={16} />
        </Button>
        <input
          type="date"
          className="h-9 rounded-lg border bg-card px-3 text-sm cursor-pointer"
          value={selectedDate}
          onChange={(e) => setSelectedDate(e.target.value)}
        />
        {!isToday(parseISO(selectedDate)) && (
          <Button variant="ghost" size="sm" onClick={goToday}>Hôm nay</Button>
        )}
      </div>

      {/* Stats */}
      <div className="grid grid-cols-3 gap-4">
        {[
          { label: 'Chờ khám', count: scheduled.length, color: 'text-blue-600', bg: 'bg-blue-50' },
          { label: 'Hoàn thành', count: completed.length, color: 'text-emerald-600', bg: 'bg-emerald-50' },
          { label: 'Đã hủy', count: cancelled.length, color: 'text-red-500', bg: 'bg-red-50' },
        ].map(({ label, count, color, bg }) => (
          <div key={label} className="bg-card border rounded-xl px-4 py-3 shadow-sm flex items-center justify-between">
            <span className="text-sm text-muted-foreground">{label}</span>
            <span className={`text-xl font-bold ${color} ${bg} px-2 py-0.5 rounded-lg`}>{count}</span>
          </div>
        ))}
      </div>

      {/* Appointment list */}
      {isLoading ? (
        <div className="flex items-center justify-center py-24 gap-3 text-muted-foreground">
          <Loader2 size={20} className="animate-spin" /> Đang tải...
        </div>
      ) : appointments.length === 0 ? (
        <div className="flex flex-col items-center justify-center py-24 gap-3 text-muted-foreground bg-card border rounded-xl">
          <Calendar size={48} className="opacity-20" />
          <p className="font-medium">Không có lịch hẹn nào</p>
          <p className="text-sm">Nhấn "Đặt lịch hẹn" để thêm lịch mới.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
          {appointments.map((appt) => (
            <AppointmentCard
              key={appt.id}
              appt={appt}
              onCancel={() => cancelMutation.mutate(appt.id)}
              onComplete={() => completeMutation.mutate(appt.id)}
            />
          ))}
        </div>
      )}

      <AppointmentModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        date={selectedDate}
      />
    </div>
  );
}
