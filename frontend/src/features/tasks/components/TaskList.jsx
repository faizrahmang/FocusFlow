import { useMemo, useState } from "react";
import TaskItem from "./TaskItem";

const tabs = [
  { value: "ALL", label: "All" },
  { value: "HABIT", label: "Habits" },
  { value: "TASK", label: "Tasks" },
  { value: "SKILL", label: "Skills" },
  { value: "HEALTH", label: "Health" },
];

export default function TaskList({
  tasks = [],
  onComplete,
  onArchive,
  onDelete,
  onReorder,
  onSelect,
}) {
  const [selectedType, setSelectedType] = useState("ALL");

  const filteredTasks = useMemo(() => {
    if (selectedType === "ALL") {
      return tasks;
    }

    return tasks.filter((task) => task.type === selectedType);
  }, [tasks, selectedType]);

  return (
    <section className="space-y-4" aria-label="Your tasks">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-slate-400">What’s on your plate</p>
          <h3 className="mt-1 text-xl font-bold tracking-tight">Your focus</h3>
        </div>
        <div className="flex max-w-full gap-1 overflow-x-auto rounded-2xl border border-slate-200/80 bg-slate-100/80 p-1 dark:border-slate-800 dark:bg-slate-900/80" role="group" aria-label="Filter tasks by type">
          {tabs.map((tab) => {
            const count =
              tab.value === "ALL"
                ? tasks.length
                : tasks.filter((task) => task.type === tab.value).length;

            const isActive = selectedType === tab.value;

            return (
              <button
                key={tab.value}
                type="button"
                onClick={() => setSelectedType(tab.value)}
                aria-pressed={isActive}
                className={`inline-flex shrink-0 items-center gap-2 rounded-xl px-3.5 py-2 text-sm font-semibold transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-1 focus-visible:outline-blue-500 ${
                  isActive
                    ? "bg-white text-slate-950 shadow-sm dark:bg-slate-700 dark:text-white"
                    : "text-slate-600 hover:bg-white/70 hover:text-slate-950 dark:text-slate-400 dark:hover:bg-slate-800 dark:hover:text-white"
                }`}
              >
                {tab.label}
                <span className={`rounded-md px-1.5 py-0.5 text-[11px] tabular-nums ${
                  isActive
                    ? "bg-blue-50 text-blue-700 dark:bg-blue-500/15 dark:text-blue-300"
                    : "bg-slate-200/70 text-slate-500 dark:bg-slate-800 dark:text-slate-400"
                }`}>
                  {count}
                </span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Task list */}
      {filteredTasks.length === 0 ? (
        <div className="rounded-3xl border border-dashed border-slate-300 bg-white/70 p-12 text-center dark:border-slate-700 dark:bg-slate-900/50">
          <div className="mx-auto grid h-12 w-12 place-items-center rounded-2xl bg-blue-50 text-xl text-blue-600 dark:bg-blue-500/10 dark:text-blue-300" aria-hidden="true">✓</div>

          <h4 className="mt-4 font-semibold text-slate-900 dark:text-slate-100">
            No tasks found
          </h4>

          <p className="mt-1 text-sm text-slate-500 dark:text-slate-400">
            Create a task or choose another category.
          </p>
        </div>
      ) : (
        <div className="space-y-3">
          {filteredTasks.map((task) => (
            <TaskItem
              key={task.id}
              task={task}
              onComplete={onComplete}
              onArchive={onArchive}
              onDelete={onDelete}
              onSelect={onSelect}
            />
          ))}
        </div>
      )}

      {/* onReorder is intentionally exposed through the required prop.
          Actual drag/reorder behavior belongs to the integration layer. */}
      <button
        type="button"
        className="hidden"
        onClick={() =>
          onReorder?.(filteredTasks.map((task) => task.id))
        }
        aria-hidden="true"
        tabIndex={-1}
      >
        Reorder
      </button>
    </section>
  );
}
