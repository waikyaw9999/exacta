import { create } from "zustand";
import { persist } from "zustand/middleware";
import { createTimeEntry } from "../api/timeTracking";
import { getApiErrorMessage } from "../api/errors";
import type { TimeEntryRequest } from "../types/api";

interface TimerState {
  clientId: number | null;
  projectId: number | null;
  description: string;
  isBillable: boolean;
  isRunning: boolean;
  startedAt: string | null;
  isMinimized: boolean;
  isSubmitting: boolean;
  error: string | null;
  lastSavedLabel: string | null;
  setClientId: (clientId: number | null) => void;
  setProjectId: (projectId: number | null) => void;
  setDescription: (description: string) => void;
  setBillable: (isBillable: boolean) => void;
  setMinimized: (isMinimized: boolean) => void;
  start: () => void;
  stop: () => Promise<void>;
  clearError: () => void;
}

export const useTimerStore = create<TimerState>()(
  persist(
    (set, get) => ({
      clientId: null,
      projectId: null,
      description: "",
      isBillable: true,
      isRunning: false,
      startedAt: null,
      isMinimized: false,
      isSubmitting: false,
      error: null,
      lastSavedLabel: null,

      setClientId: (clientId) => {
        const { isRunning } = get();
        if (isRunning) {
          return;
        }
        set({ clientId, projectId: null, error: null });
      },

      setProjectId: (projectId) => {
        if (get().isRunning) {
          return;
        }
        set({ projectId, error: null });
      },

      setDescription: (description) => {
        set({ description });
      },

      setBillable: (isBillable) => {
        if (get().isRunning) {
          return;
        }
        set({ isBillable });
      },

      setMinimized: (isMinimized) => {
        set({ isMinimized });
      },

      start: () => {
        const { isRunning, projectId, clientId } = get();
        if (isRunning || projectId == null || clientId == null) {
          set({
            error:
              projectId == null || clientId == null
                ? "Select a client and project to start tracking"
                : null,
          });
          return;
        }

        set({
          isRunning: true,
          startedAt: new Date().toISOString(),
          error: null,
          lastSavedLabel: null,
        });
      },

      stop: async () => {
        const { isRunning, startedAt, projectId, description, isBillable, isSubmitting } = get();
        if (!isRunning || !startedAt || projectId == null || isSubmitting) {
          return;
        }

        const endTime = new Date().toISOString();
        const payload: TimeEntryRequest = {
          projectId,
          startTime: startedAt,
          endTime,
          description: description.trim() ? description.trim() : null,
          isBillable,
          status: "STOPPED",
        };

        set({ isSubmitting: true, error: null });

        try {
          const saved = await createTimeEntry(payload);
          const minutes = saved.durationMinutes ?? 0;
          set({
            isRunning: false,
            startedAt: null,
            isSubmitting: false,
            description: "",
            lastSavedLabel: `Saved ${minutes} min to ${saved.projectName}`,
          });
        } catch (error) {
          set({
            isSubmitting: false,
            error: getApiErrorMessage(
              error,
              "Could not save this time entry. The timer is still running.",
            ),
          });
        }
      },

      clearError: () => {
        set({ error: null });
      },
    }),
    {
      name: "exacta.timer",
      partialize: (state) => ({
        clientId: state.clientId,
        projectId: state.projectId,
        description: state.description,
        isBillable: state.isBillable,
        isRunning: state.isRunning,
        startedAt: state.startedAt,
        isMinimized: state.isMinimized,
      }),
    },
  ),
);
