import { apiBase } from '@/lib/api';
import { api } from '@/lib/api';
import { format } from 'date-fns';

export interface TopBmiPatient {
  patientCode: string;
  fullName: string;
  weight: number;
  height: number;
  bmi: number;
  measuredAt: string;
}

export interface HighBpRate {
  total: number;
  highBpCount: number;
  percentage: number;
}

export interface ActiveMedication {
  medicationName: string;
  totalPrescription: number;
}

export const reportApi = {
  getTopBmi: (limit = 10) =>
    apiBase.get<TopBmiPatient[]>('/reports/top-bmi', { params: { limit } }).then((r) => r.data),

  getHighBpRate: () =>
    apiBase.get<Record<string, number>>('/reports/high-blood-pressure-rate').then((r) => r.data),

  getActiveMedications: () =>
    apiBase.get<Record<string, unknown>[]>('/reports/active-medications').then((r) => r.data),
};

export const getTodayAppointments = async () => {
  const today = format(new Date(), 'yyyy-MM-dd');
  const r = await api.get<unknown[]>('/appointments', { params: { date: today } });
  return r.data;
};

export const getPatientStats = () =>
  api.get<{ totalElements: number }>('/patients', { params: { page: 0, size: 1 } }).then((r) => r.data);
