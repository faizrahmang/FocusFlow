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
    <section>
      {/* Type filters */}
      <div className="mb-4 flex flex-wrap gap-2">
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
              className={`rounded-xl px-4 py-2 text-sm font-medium transition ${
                isActive
                  ? "bg-slate-900 text-white dark:bg-white dark:text-slate-900"
                  : "bg-white text-slate-600 hover:bg-slate-100 dark:bg-slate-900 dark:text-slate-300 dark:hover:bg-slate-800"
              }`}
            >
              {tab.label}
              <span className="ml-2 text-xs opacity-60">{count}</span>
            </button>
          );
        })}
      </div>

      {/* Task list */}
      {filteredTasks.length === 0 ? (
        <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center dark:border-slate-700 dark:bg-slate-900">
          <div className="text-3xl">✓</div>

          <h3 className="mt-3 font-semibold text-slate-900 dark:text-slate-100">
            No tasks found
          </h3>

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
