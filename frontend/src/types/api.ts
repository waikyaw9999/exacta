export type Role = "ADMIN" | "MEMBER";

export type ProjectStatus = "ACTIVE" | "ON_HOLD" | "COMPLETED" | "ARCHIVED";

export type TimeEntryStatus = "RUNNING" | "STOPPED" | "SUBMITTED" | "BILLED";

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
  hourlyRate: number;
}

export interface Client {
  id: number;
  name: string;
  contactEmail: string | null;
  company: string;
}

export interface Project {
  id: number;
  name: string;
  clientId: number;
  clientName: string;
  status: ProjectStatus;
}

export interface TimeEntryRequest {
  projectId: number;
  startTime: string;
  endTime: string;
  description: string | null;
  isBillable: boolean;
  status: TimeEntryStatus;
}

export interface TimeEntry {
  id: number;
  userId: number;
  userName: string;
  projectId: number;
  projectName: string;
  clientId: number;
  clientName: string;
  startTime: string;
  endTime: string | null;
  durationMinutes: number | null;
  description: string | null;
  isBillable: boolean;
  status: TimeEntryStatus;
}
