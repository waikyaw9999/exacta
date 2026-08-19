interface TimerSelectProps {
  id: string;
  label: string;
  value: string;
  disabled?: boolean;
  options: ReadonlyArray<{ value: string; label: string }>;
  placeholder: string;
  onChange: (value: string) => void;
}

export function TimerSelect({
  id,
  label,
  value,
  disabled = false,
  options,
  placeholder,
  onChange,
}: TimerSelectProps) {
  return (
    <label className="block" htmlFor={id}>
      <span className="mb-1 block text-[11px] font-medium uppercase tracking-wider text-slate-400">
        {label}
      </span>
      <select
        id={id}
        value={value}
        disabled={disabled}
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-lg border border-white/10 bg-slate-950/80 px-3 py-2 text-sm text-slate-100 outline-none transition focus:border-blue-400/60 focus:ring-2 focus:ring-blue-500/30 disabled:cursor-not-allowed disabled:opacity-60"
      >
        <option value="">{placeholder}</option>
        {options.map((option) => (
          <option key={option.value} value={option.value}>
            {option.label}
          </option>
        ))}
      </select>
    </label>
  );
}
