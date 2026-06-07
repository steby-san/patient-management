import { api } from '@/lib/api';

export interface Patient {
  id: number;
  patientCode: string;
  fullName: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  phoneNumber: string;
  email: string;
  address: string;
  createdAt: string;
}

export interface PatientCreateDto {
  fullName: string;
  dateOfBirth: string;
  gender: string;
  phoneNumber: string;
  email?: string;
  address: string;
}

export interface PageResponse<T> {
  data: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export const patientApi = {
  getAll: (page = 0, size = 10, search?: string) =>
    api.get<PageResponse<Patient>>('/patients', {
      params: { page, size, search: search || undefined },
    }).then((r) => r.data),

  getById: (id: number) =>
    api.get<Patient>(`/patients/${id}`).then((r) => r.data),

  create: (dto: PatientCreateDto) =>
    api.post<Patient>('/patients', dto).then((r) => r.data),

  update: (id: number, dto: PatientCreateDto) =>
    api.put<Patient>(`/patients/${id}`, dto).then((r) => r.data),

  delete: (id: number) =>
    api.delete(`/patients/${id}`),
};
