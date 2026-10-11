export default function DueSoonBanner({ tasks = [], onDismiss }) {
  if (!tasks.length) {
    return null;
  }

  return (
    <div className="mb-5 rounded-2xl border border-amber-200 bg-amber-50 p-4 dark:border-amber-500/20 dark:bg-amber-500/10">
      <div className="flex items-start gap-3">
        <span className="text-xl" aria-hidden="true">
          ⏰
        </span>

        <div className="min-w-0 flex-1">
          <h3 className="font-semibold text-amber-900 dark:text-amber-200">
            Due soon
          </h3>

          <div className="mt-2 space-y-2">
            {tasks.map((task) => (
              <div
                key={task.id}
                className="flex items-center justify-between gap-3 text-sm text-amber-800 dark:text-amber-100"
              >
                <div className="min-w-0">
                  <p className="truncate font-medium">{task.title}</p>

                  {task.dueDate && (
                    <p className="text-xs opacity-70">
                      {task.dueDate}
                      {task.dueTime ? ` · ${task.dueTime}` : ""}
                    </p>
                  )}
                </div>

                <button
                  type="button"
                  onClick={() => onDismiss?.(task.id)}
                  className="shrink-0 rounded-lg px-2 py-1 text-xs font-semibold hover:bg-amber-100 dark:hover:bg-amber-500/20"
                >
                  Dismiss
                </button>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
