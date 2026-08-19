import type { Client, Project, TimeEntry, TimeEntryRequest } from "../types/api";
import { apiClient } from "./client";

export async function fetchClients(): Promise<Client[]> {
  const { data } = await apiClient.get<Client[]>("/clients");
  return data;
}

export async function fetchProjects(clientId?: number): Promise<Project[]> {
  const { data } = await apiClient.get<Project[]>("/projects", {
    params: { clientId, status: "ACTIVE" },
  });
  return data;
}

export async function fetchTimeEntries(): Promise<TimeEntry[]> {
  const { data } = await apiClient.get<TimeEntry[]>("/time-entries");
  return data;
}

export async function createTimeEntry(payload: TimeEntryRequest): Promise<TimeEntry> {
  const { data } = await apiClient.post<TimeEntry>("/time-entries", payload);
  return data;
}
