export default function SummaryCards({ summary = { completed: 0, total: 0, completionRate: 0, expectedVsActualMinutes: { expected: 0, actual: 0 } } }) {
  const cards = [
    ["Completed", summary.completed, "of " + summary.total],
    ["Completion rate", `${summary.completionRate}%`, "overall"],
    ["Expected time", `${summary.expectedVsActualMinutes?.expected ?? 0}m`, "planned"],
    ["Actual time", `${summary.expectedVsActualMinutes?.actual ?? 0}m`, "logged"],
  ];

  return (
    <div className="grid grid-cols-2 gap-3 lg:grid-cols-4">
      {cards.map(([label, value, hint]) => (
        <div key={label} className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm dark:border-slate-800 dark:bg-slate-900">
          <p className="text-xs font-medium text-slate-500">{label}</p>
          <p className="mt-2 text-2xl font-bold text-slate-900 dark:text-white">{value}</p>
          <p className="mt-1 text-xs text-slate-400">{hint}</p>
        </div>
      ))}
    </div>
  );
}