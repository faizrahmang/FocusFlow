const typeLabels = { HABIT: "Habit", TASK: "Task", SKILL: "Skill", HEALTH: "Health" };

export default function GoalList({ goals = [], onSelect, onDelete }) {
  return (
    <section className="space-y-3">
      {goals.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center dark:border-slate-700 dark:bg-slate-900">
          <h3 className="font-semibold text-slate-900 dark:text-white">No goals yet</h3>
          <p className="mt-1 text-sm text-slate-500">Create a goal to start tracking progress.</p>
        </div>
      ) : goals.map((goal) => {
        const progress = Math.max(0, Math.min(100, Number(goal.progressPercent || 0)));
        return (
          <article key={goal.id} className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm dark:border-slate-800 dark:bg-slate-900">
            <div className="flex items-start justify-between gap-4">
              <button onClick={() => onSelect?.(goal.id)} className="text-left">
                <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">{typeLabels[goal.type] || goal.type}</p>
                <h3 className="mt-1 font-semibold text-slate-900 hover:text-blue-600 dark:text-white">{goal.title}</h3>
              </button>
              <button onClick={() => onDelete?.(goal.id)} className="rounded-lg px-2 py-1 text-xs text-rose-600 hover:bg-rose-50 dark:hover:bg-rose-500/10">Delete</button>
            </div>

            <div className="mt-5">
              <div className="mb-2 flex justify-between text-xs">
                <span className="text-slate-500">{goal.linkedTaskIds?.length || 0} linked tasks</span>
                <strong className="text-slate-700 dark:text-slate-200">{progress}%</strong>
              </div>
              <div className="h-2 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
                <div className="h-full rounded-full bg-blue-600 transition-all duration-700" style={{ width: `${progress}%` }} />
              </div>
            </div>

            {goal.targetDate && <p className="mt-3 text-xs text-slate-400">Target: {goal.targetDate}</p>}
          </article>
        );
      })}
    </section>
  );
}