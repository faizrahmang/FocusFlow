const typeMeta = {
  HABIT: { label: "Habit", icon: "↻", tone: "violet" },
  TASK: { label: "Task", icon: "✓", tone: "blue" },
  SKILL: { label: "Skill", icon: "◆", tone: "emerald" },
  HEALTH: { label: "Health", icon: "♥", tone: "rose" },
};

const toneClasses = {
  violet: "bg-violet-50 text-violet-700 dark:bg-violet-500/10 dark:text-violet-300",
  blue: "bg-blue-50 text-blue-700 dark:bg-blue-500/10 dark:text-blue-300",
  emerald: "bg-emerald-50 text-emerald-700 dark:bg-emerald-500/10 dark:text-emerald-300",
  rose: "bg-rose-50 text-rose-700 dark:bg-rose-500/10 dark:text-rose-300",
};

function Priority({ value = 1 }) {
  return (
    <div className="flex items-center gap-1" aria-label={`Priority ${value} of 5`}>
      {Array.from({ length: 5 }, (_, i) => (
        <span
          key={i}
          className={`h-2 w-2 rounded-full ${i < value ? "bg-amber-500" : "bg-slate-200 dark:bg-slate-700"}`}
        />
      ))}
    </div>
  );
}

function recurrenceLabel(recurrence) {
  if (!recurrence || recurrence.type === "NONE") return null;
  if (recurrence.type === "DAILY") return "Daily";
  if (recurrence.type === "WEEKLY") return "Weekly";
  if (recurrence.type === "CUSTOM_DAYS") return `${recurrence.daysOfWeek?.length || 0} days/week`;
  if (recurrence.type === "FOR_N_DAYS") return `${recurrence.occurrences || 0} days`;
  return null;
}

export default function TaskItem({
  task,
  onComplete,
  onArchive,
  onDelete,
  onSelect,
}) {
  const meta = typeMeta[task.type] || typeMeta.TASK;
  const recurrence = recurrenceLabel(task.recurrence);

  return (
    <article
      className="group rounded-2xl border border-slate-200 bg-white p-4 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md dark:border-slate-800 dark:bg-slate-900"
    >
      <div className="flex gap-4">
        <button
          onClick={() => onComplete?.(task)}
          className="mt-1 grid h-6 w-6 shrink-0 place-items-center rounded-full border-2 border-slate-300 text-xs text-transparent transition hover:border-emerald-500 hover:bg-emerald-50 hover:text-emerald-600 dark:border-slate-600 dark:hover:bg-emerald-500/10"
          aria-label={`Complete ${task.title}`}
        >
          ✓
        </button>

        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-start justify-between gap-2">
            <button onClick={() => onSelect?.(task)} className="text-left">
              <h3 className="font-semibold text-slate-900 hover:text-blue-600 dark:text-slate-100 dark:hover:text-blue-400">
                {task.title}
              </h3>
            </button>

            <span className={`rounded-full px-2.5 py-1 text-xs font-medium ${toneClasses[meta.tone]}`}>
              {meta.icon} {meta.label}
            </span>
          </div>

          {task.description && (
            <p className="mt-1 line-clamp-2 text-sm text-slate-500 dark:text-slate-400">
              {task.description}
            </p>
          )}

          <div className="mt-4 flex flex-wrap items-center gap-x-4 gap-y-2 text-xs text-slate-500 dark:text-slate-400">
            {task.dueDate && <span>📅 {task.dueDate}</span>}
            {task.dueTime && <span>◷ {task.dueTime}</span>}
            {task.expectedDurationMinutes != null && <span>⏱ {task.expectedDurationMinutes} min</span>}
            {recurrence && <span className="rounded-full bg-slate-100 px-2 py-1 dark:bg-slate-800">{recurrence}</span>}
            <Priority value={task.priority} />
          </div>

          <div className="mt-4 flex items-center justify-between border-t border-slate-100 pt-3 dark:border-slate-800">
            <span className="text-xs text-slate-400">#{task.id}</span>
            <div className="flex gap-1 opacity-70 transition group-hover:opacity-100">
              <button onClick={() => onSelect?.(task)} className="rounded-lg px-2 py-1 text-xs font-medium text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800">
                Open
              </button>
              <button onClick={() => onArchive?.(task.id)} className="rounded-lg px-2 py-1 text-xs font-medium text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800">
                Archive
              </button>
              <button onClick={() => onDelete?.(task.id)} className="rounded-lg px-2 py-1 text-xs font-medium text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-500/10">
                Delete
              </button>
            </div>
          </div>
        </div>
      </div>
    </article>
  );
}