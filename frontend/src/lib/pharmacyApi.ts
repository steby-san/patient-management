import { api } from '@/lib/api';
import { apiBase } from '@/lib/api';

export interface Medication {
  id: number;
  code: string;
  name: string;
  activeIngredient: string;
  unit: string;
}

export interface MedicationRequestDto {
  code: string;
  name: string;
  activeIngredient?: string;
  unit?: string;
}

export interface PrescriptionItemDto {
  medicationId: number;
  medicationName?: string;
  dosage?: string;
  quantity: number;
}

export interface PrescriptionCreateDto {
  patientId: number;
  doctorName?: string;
  startDate: string;
  endDate: string;
  diagnosis?: string;
  items: PrescriptionItemDto[];
}

export interface PrescriptionResponseDto {
  id: number;
  patientId: number;
  patientName: string;
  doctorName: string;
  startDate: string;
  endDate: string;
  diagnosis: string;
  createdAt: string;
  items: PrescriptionItemDto[];
}

export const medicationApi = {
  getAll: () =>
    apiBase.get<Medication[]>('/medications').then((r) => r.data),
  create: (dto: MedicationRequestDto) =>
    apiBase.post<Medication>('/medications', dto).then((r) => r.data),
  update: (id: number, dto: MedicationRequestDto) =>
    apiBase.put<Medication>(`/medications/${id}`, dto).then((r) => r.data),
  delete: (id: number) =>
    apiBase.delete(`/medications/${id}`),
};

export const prescriptionApi = {
  getByPatient: (patientId: number) =>
    api.get<PrescriptionResponseDto[]>(`/prescriptions/patient/${patientId}`).then((r) => r.data),
  getDetail: (id: number) =>
    api.get<PrescriptionResponseDto>(`/prescriptions/${id}`).then((r) => r.data),
  create: (dto: PrescriptionCreateDto) =>
    api.post<PrescriptionResponseDto>('/prescriptions', dto).then((r) => r.data),
  update: (id: number, dto: PrescriptionCreateDto) =>
    api.put<PrescriptionResponseDto>(`/prescriptions/${id}`, dto).then((r) => r.data),
  delete: (id: number) =>
    api.delete(`/prescriptions/${id}`),
};
