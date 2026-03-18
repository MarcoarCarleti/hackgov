type KpiCardProps = {
  label: string;
  value: string | number;
  tone?: "default" | "warn" | "good";
};

export default function KpiCard({ label, value, tone = "default" }: KpiCardProps) {
  const toneClass =
    tone === "warn"
      ? "border-amber-300 bg-amber-50"
      : tone === "good"
        ? "border-emerald-300 bg-emerald-50"
        : "border-sky-300 bg-sky-50";

  return (
    <div className={`rounded-2xl border p-4 shadow-sm ${toneClass}`}>
      <p className="text-xs uppercase tracking-wide text-slate-600">{label}</p>
      <p className="mt-2 text-2xl font-semibold">{value}</p>
    </div>
  );
}
