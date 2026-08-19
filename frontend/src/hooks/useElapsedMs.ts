import { useEffect, useState } from "react";

export function useElapsedMs(startedAt: string | null, isRunning: boolean): number {
  const [now, setNow] = useState(() => Date.now());

  useEffect(() => {
    if (!isRunning) {
      return;
    }

    const intervalId = window.setInterval(() => {
      setNow(Date.now());
    }, 250);

    return () => {
      window.clearInterval(intervalId);
    };
  }, [isRunning]);

  if (!isRunning || !startedAt) {
    return 0;
  }

  const startedMs = Date.parse(startedAt);
  if (!Number.isFinite(startedMs)) {
    return 0;
  }

  return Math.max(0, now - startedMs);
}
