import { type FormEvent, type ReactNode } from "react";
import { Link } from "react-router-dom";

interface AuthLayoutProps {
  title: string;
  subtitle: string;
  footer: ReactNode;
  children: ReactNode;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
  submitLabel: string;
  isSubmitting: boolean;
  error: string | null;
}

export function AuthLayout({
  title,
  subtitle,
  footer,
  children,
  onSubmit,
  submitLabel,
  isSubmitting,
  error,
}: AuthLayoutProps) {
  return (
    <main className="flex min-h-screen items-center justify-center px-4 py-10">
      <section className="w-full max-w-md rounded-2xl border border-white/10 bg-slate-900/80 p-6 shadow-2xl shadow-black/40">
        <Link to="/" className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-300">
          Exacta
        </Link>
        <h1 className="mt-3 text-2xl font-semibold tracking-tight text-white">{title}</h1>
        <p className="mt-2 text-sm text-slate-400">{subtitle}</p>
        <form className="mt-6 space-y-4" onSubmit={onSubmit}>
          {children}
          {error ? <p className="text-sm text-rose-300">{error}</p> : null}
          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full rounded-xl bg-blue-500 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-400 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isSubmitting ? "Please wait…" : submitLabel}
          </button>
        </form>
        <p className="mt-5 text-center text-sm text-slate-400">{footer}</p>
      </section>
    </main>
  );
}

interface AuthFieldProps {
  id: string;
  label: string;
  type?: string;
  value: string;
  autoComplete?: string;
  required?: boolean;
  minLength?: number;
  step?: string;
  onChange: (value: string) => void;
}

export function AuthField({
  id,
  label,
  type = "text",
  value,
  autoComplete,
  required = true,
  minLength,
  step,
  onChange,
}: AuthFieldProps) {
  return (
    <label className="block" htmlFor={id}>
      <span className="mb-1 block text-sm text-slate-300">{label}</span>
      <input
        id={id}
        type={type}
        value={value}
        autoComplete={autoComplete}
        required={required}
        minLength={minLength}
        step={step}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-lg border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white outline-none transition focus:border-blue-400/60 focus:ring-2 focus:ring-blue-500/30"
      />
    </label>
  );
}
