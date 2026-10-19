const typeLabels = {
  HABIT: "Habit",
  TASK: "Task",
  SKILL: "Skill",
  HEALTH: "Health",
};

function formatCompletedAt(value) {
  if (!value) return "Completion date unavailable";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "Completion date unavailable";
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(date);
}

export default function CompletionHistory({ items = [], loading = false, error = null }) {
  return (
    <section className="space-y-4" aria-labelledby="completion-history-title">
      <div className="flex items-end justify-between gap-3">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-slate-400">Your wins</p>
          <h3 id="completion-history-title" className="mt-1 text-xl font-bold tracking-tight">Completed</h3>
        </div>
        <span className="rounded-full bg-emerald-50 px-3 py-1 text-sm font-semibold tabular-nums text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-300">
          {items.length} {items.length === 1 ? "completion" : "completions"}
        </span>
      </div>

      {error && (
        <p role="alert" className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-900/60 dark:bg-red-950/40 dark:text-red-300">
          Could not load completion history: {error}
        </p>
      )}

      {loading ? (
        <p role="status" className="rounded-2xl border border-slate-200 bg-white p-5 text-sm text-slate-500 dark:border-slate-800 dark:bg-slate-900 dark:text-slate-400">
          Loading completion history…
        </p>
      ) : items.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-white/70 p-6 text-center dark:border-slate-700 dark:bg-slate-900/50">
          <p className="font-medium text-slate-800 dark:text-slate-200">Your completed tasks will appear here.</p>
          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">Finish a task to start building your history.</p>
        </div>
      ) : (
        <ol className="max-h-[28rem] divide-y divide-slate-100 overflow-y-auto rounded-2xl border border-slate-200/80 bg-white dark:divide-slate-800 dark:border-slate-800 dark:bg-slate-900">
          {items.map((item) => (
            <li key={item.id} className="flex gap-3 px-4 py-4 sm:px-5">
              <span className="mt-0.5 grid h-8 w-8 shrink-0 place-items-center rounded-full bg-emerald-50 text-sm font-bold text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-300" aria-hidden="true">
                ✓
              </span>
              <div className="min-w-0 flex-1">
                <div className="flex flex-wrap items-baseline justify-between gap-x-4 gap-y-1">
                  <h4 className="font-semibold text-slate-900 dark:text-slate-100">{item.taskTitle || `Task #${item.taskId}`}</h4>
                  <time className="text-xs text-slate-500 dark:text-slate-400">{formatCompletedAt(item.completedAt)}</time>
                </div>
                <div className="mt-1 flex flex-wrap items-center gap-x-3 gap-y-1 text-xs text-slate-500 dark:text-slate-400">
                  {item.taskType && <span>{typeLabels[item.taskType] || item.taskType}</span>}
                  {item.actualDurationMinutes != null && <span>{item.actualDurationMinutes} min logged</span>}
                  {item.photoUrl && (
                    <a href={item.photoUrl} target="_blank" rel="noreferrer" className="font-medium text-blue-600 hover:underline dark:text-blue-400">
                      View proof
                    </a>
                  )}
                </div>
                {item.note && <p className="mt-2 whitespace-pre-wrap text-sm text-slate-600 dark:text-slate-300">{item.note}</p>}
              </div>
            </li>
          ))}
        </ol>
      )}
    </section>
  );
}
