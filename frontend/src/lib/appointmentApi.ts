import { api } from '@/lib/api';

export interface Appointment {
  id: number;
  patientId: number;
  patientName: string;
  appointmentTime: string;
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | 'PENDING';
  reason: string;
  doctorName: string;
}

export interface AppointmentCreateDto {
  patientId: number;
  appointmentTime: string;
  reason?: string;
  doctorName?: string;
}

export interface HealthMetric {
  id: number;
  patientId: number;
  weight: number | null;
  height: number | null;
  systolic: number | null;
  diastolic: number | null;
  heartRate: number | null;
  measuredAt: string;
}

export interface HealthMetricInputDto {
  patientId: number;
  weight?: number;
  height?: number;
  systolic?: number;
  diastolic?: number;
  heartRate?: number;
  measuredAt?: string;
}

export const appointmentApi = {
  getByDate: (date: string) =>
    api.get<Appointment[]>('/appointments', { params: { date } }).then((r) => r.data),

  getById: (id: number) =>
    api.get<Appointment>(`/appointments/${id}`).then((r) => r.data),

  create: (dto: AppointmentCreateDto) =>
    api.post<Appointment>('/appointments', dto).then((r) => r.data),

  update: (id: number, dto: AppointmentCreateDto) =>
    api.put<Appointment>(`/appointments/${id}`, dto).then((r) => r.data),

  updateStatus: (id: number, status: string) =>
    api.put<Appointment>(`/appointments/${id}/status`, null, { params: { status } }).then((r) => r.data),

  cancel: (id: number) =>
    api.delete(`/appointments/${id}`),
};

export const healthMetricApi = {
  getByPatient: (patientId: number) =>
    api.get<HealthMetric[]>(`/health-metrics/patient/${patientId}`).then((r) => r.data),

  getById: (id: number) =>
    api.get<HealthMetric>(`/health-metrics/${id}`).then((r) => r.data),

  add: (dto: HealthMetricInputDto) =>
    api.post<HealthMetric>('/health-metrics', dto).then((r) => r.data),

  update: (id: number, dto: HealthMetricInputDto) =>
    api.put<HealthMetric>(`/health-metrics/${id}`, dto).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/health-metrics/${id}`),
};
