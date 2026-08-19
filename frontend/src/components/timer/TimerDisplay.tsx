interface TimerDisplayProps {
  elapsedLabel: string;
  isRunning: boolean;
}

export function TimerDisplay({ elapsedLabel, isRunning }: TimerDisplayProps) {
  return (
    <div className="flex items-center gap-2">
      <span
        className={`h-2 w-2 rounded-full ${
          isRunning ? "animate-pulse bg-emerald-400" : "bg-slate-500"
        }`}
        aria-hidden="true"
      />
      <p
        className="font-mono text-2xl font-semibold tabular-nums tracking-tight text-white sm:text-3xl"
        aria-live="polite"
        aria-label={isRunning ? `Timer running ${elapsedLabel}` : `Timer stopped ${elapsedLabel}`}
      >
        {elapsedLabel}
      </p>
    </div>
  );
}
