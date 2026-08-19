import { useMemo } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { fetchClients, fetchProjects } from "../../api/timeTracking";
import { useElapsedMs } from "../../hooks/useElapsedMs";
import { formatElapsed } from "../../lib/formatElapsed";
import { useTimerStore } from "../../stores/timerStore";
import { TimerDisplay } from "./TimerDisplay";
import { TimerSelect } from "./TimerSelect";

export function FloatingTimer() {
  const queryClient = useQueryClient();
  const clientId = useTimerStore((state) => state.clientId);
  const projectId = useTimerStore((state) => state.projectId);
  const description = useTimerStore((state) => state.description);
  const isBillable = useTimerStore((state) => state.isBillable);
  const isRunning = useTimerStore((state) => state.isRunning);
  const startedAt = useTimerStore((state) => state.startedAt);
  const isMinimized = useTimerStore((state) => state.isMinimized);
  const isSubmitting = useTimerStore((state) => state.isSubmitting);
  const error = useTimerStore((state) => state.error);
  const lastSavedLabel = useTimerStore((state) => state.lastSavedLabel);
  const setClientId = useTimerStore((state) => state.setClientId);
  const setProjectId = useTimerStore((state) => state.setProjectId);
  const setDescription = useTimerStore((state) => state.setDescription);
  const setBillable = useTimerStore((state) => state.setBillable);
  const setMinimized = useTimerStore((state) => state.setMinimized);
  const start = useTimerStore((state) => state.start);
  const stop = useTimerStore((state) => state.stop);

  const elapsedMs = useElapsedMs(startedAt, isRunning);
  const elapsedLabel = formatElapsed(elapsedMs);

  const clientsQuery = useQuery({
    queryKey: ["clients"],
    queryFn: fetchClients,
    staleTime: 60_000,
  });

  const projectsQuery = useQuery({
    queryKey: ["projects"],
    queryFn: () => fetchProjects(),
    staleTime: 60_000,
  });

  const clientOptions = useMemo(
    () =>
      (clientsQuery.data ?? []).map((client) => ({
        value: String(client.id),
        label: `${client.name} · ${client.company}`,
      })),
    [clientsQuery.data],
  );

  const projectOptions = useMemo(
    () =>
      (projectsQuery.data ?? [])
        .filter((project) => clientId != null && project.clientId === clientId)
        .map((project) => ({
          value: String(project.id),
          label: project.name,
        })),
    [projectsQuery.data, clientId],
  );

  const selectedProject = (projectsQuery.data ?? []).find((project) => project.id === projectId);
  const canStart = clientId != null && projectId != null && !isSubmitting;
  const fieldsLocked = isRunning || isSubmitting;

  async function handleToggle() {
    if (isRunning) {
      await stop();
      await queryClient.invalidateQueries({ queryKey: ["time-entries"] });
      await queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      return;
    }
    start();
  }

  if (isMinimized) {
    return (
      <div className="fixed bottom-3 right-3 z-50 flex items-center gap-2 sm:bottom-5 sm:right-5">
        <button
          type="button"
          onClick={() => setMinimized(false)}
          className="flex items-center gap-3 rounded-full border border-white/10 bg-slate-900/95 px-4 py-2.5 text-sm shadow-2xl shadow-black/40 backdrop-blur-md transition hover:border-white/20"
          aria-label="Expand timer"
        >
          <span
            className={`h-2 w-2 rounded-full ${isRunning ? "animate-pulse bg-emerald-400" : "bg-slate-500"}`}
          />
          <span className="font-mono tabular-nums text-white">{elapsedLabel}</span>
          <span className="hidden max-w-[10rem] truncate text-slate-400 sm:inline">
            {selectedProject?.name ?? "Timer"}
          </span>
        </button>
        {isRunning ? (
          <button
            type="button"
            onClick={() => {
              void handleToggle();
            }}
            disabled={isSubmitting}
            className="rounded-full bg-rose-500 px-4 py-2.5 text-sm font-semibold text-white shadow-lg shadow-rose-950/40 transition hover:bg-rose-400 disabled:opacity-50"
          >
            {isSubmitting ? "Saving…" : "Stop"}
          </button>
        ) : null}
      </div>
    );
  }

  return (
    <aside
      className="fixed inset-x-3 bottom-3 z-50 rounded-2xl border border-white/10 bg-slate-900/95 p-4 shadow-2xl shadow-black/50 backdrop-blur-md sm:inset-x-auto sm:bottom-5 sm:right-5 sm:w-[26.5rem]"
      aria-label="Time tracker"
    >
      <div className="mb-4 flex items-start justify-between gap-3">
        <div>
          <p className="text-[11px] font-semibold uppercase tracking-[0.18em] text-blue-300">
            {isRunning ? "Tracking" : "Ready"}
          </p>
          <TimerDisplay elapsedLabel={elapsedLabel} isRunning={isRunning} />
        </div>
        <button
          type="button"
          onClick={() => setMinimized(true)}
          className="rounded-lg px-2 py-1 text-xs text-slate-400 transition hover:bg-white/5 hover:text-slate-200"
        >
          Minimize
        </button>
      </div>

      <div className="space-y-3">
        <TimerSelect
          id="timer-client"
          label="Client"
          placeholder="Select client"
          value={clientId == null ? "" : String(clientId)}
          disabled={fieldsLocked}
          options={clientOptions}
          onChange={(value) => setClientId(value ? Number(value) : null)}
        />
        <TimerSelect
          id="timer-project"
          label="Project"
          placeholder={clientId == null ? "Select a client first" : "Select project"}
          value={projectId == null ? "" : String(projectId)}
          disabled={fieldsLocked || clientId == null}
          options={projectOptions}
          onChange={(value) => setProjectId(value ? Number(value) : null)}
        />

        <label className="block" htmlFor="timer-description">
          <span className="mb-1 block text-[11px] font-medium uppercase tracking-wider text-slate-400">
            Description
          </span>
          <input
            id="timer-description"
            type="text"
            maxLength={1000}
            value={description}
            placeholder="What are you working on?"
            onChange={(event) => setDescription(event.target.value)}
            className="w-full rounded-lg border border-white/10 bg-slate-950/80 px-3 py-2 text-sm text-slate-100 outline-none transition placeholder:text-slate-500 focus:border-blue-400/60 focus:ring-2 focus:ring-blue-500/30"
          />
        </label>

        <label className="flex items-center gap-2 text-sm text-slate-300">
          <input
            type="checkbox"
            checked={isBillable}
            disabled={fieldsLocked}
            onChange={(event) => setBillable(event.target.checked)}
            className="h-4 w-4 rounded border-white/20 bg-slate-950 text-blue-500 focus:ring-blue-500/40"
          />
          Billable
        </label>
      </div>

      {error ? <p className="mt-3 text-xs text-rose-300">{error}</p> : null}
      {lastSavedLabel && !isRunning ? (
        <p className="mt-3 text-xs text-emerald-300">{lastSavedLabel}</p>
      ) : null}
      {clientsQuery.isError ? (
        <p className="mt-3 text-xs text-rose-300">
          Could not load clients. Confirm you are signed in and the API is running.
        </p>
      ) : null}

      <button
        type="button"
        onClick={() => {
          void handleToggle();
        }}
        disabled={!isRunning && !canStart}
        className={`mt-4 flex w-full items-center justify-center rounded-xl px-4 py-3 text-sm font-semibold transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 disabled:cursor-not-allowed disabled:opacity-50 ${
          isRunning
            ? "bg-rose-500 text-white hover:bg-rose-400 focus-visible:outline-rose-300"
            : "bg-blue-500 text-white hover:bg-blue-400 focus-visible:outline-blue-300"
        }`}
      >
        {isSubmitting ? "Saving…" : isRunning ? "Stop" : "Start"}
      </button>
    </aside>
  );
}
