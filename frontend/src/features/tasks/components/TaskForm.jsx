import { useEffect, useState } from "react";

const empty = {
  title: "",
  description: "",
  type: "TASK",
  priority: 3,
  goalId: null,
  dueDate: "",
  dueTime: "",
  expectedDurationMinutes: 30,
  reminderDateTime: "",
  recurrence: { type: "NONE", daysOfWeek: [], occurrences: null, endDate: "" },
};

const types = [
  ["HABIT", "Habit", "↻", "Build a consistent routine"],
  ["TASK", "Task", "✓", "Finish a specific to-do"],
  ["SKILL", "Skill", "◆", "Grow a skill over time"],
  ["HEALTH", "Health", "♥", "Support your well-being"],
];

const days = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"];

export default function TaskForm({ initialValues, goals = [], onSubmit, onCancel }) {
  const [form, setForm] = useState(initialValues || empty);

  useEffect(() => setForm(initialValues || empty), [initialValues]);

  const set = (key, value) => setForm((current) => ({ ...current, [key]: value }));
  const setRecurrence = (patch) =>
    setForm((current) => ({ ...current, recurrence: { ...current.recurrence, ...patch } }));

  const toggleDay = (day) => {
    const current = form.recurrence.daysOfWeek || [];
    setRecurrence({
      daysOfWeek: current.includes(day) ? current.filter((d) => d !== day) : [...current, day],
    });
  };

  const submit = (event) => {
    event.preventDefault();
    onSubmit?.({
      ...form,
      priority: Number(form.priority),
      expectedDurationMinutes: Number(form.expectedDurationMinutes),
      goalId: form.goalId ? Number(form.goalId) : null,
      recurrence: {
        ...form.recurrence,
        occurrences: form.recurrence.occurrences ? Number(form.recurrence.occurrences) : null,
      },
    });
  };

  return (
    <form onSubmit={submit} className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm dark:border-slate-800 dark:bg-slate-900">
      <div className="mb-6">
        <h2 className="text-lg font-bold text-slate-900 dark:text-slate-100">
          {initialValues ? "Edit task" : "Create task"}
        </h2>
        <p className="mt-1 text-sm text-slate-500">Set the details and schedule for this item.</p>
      </div>

      <div className="grid gap-5 md:grid-cols-2">
        <label className="md:col-span-2">
          <span className="label">Title</span>
          <input required value={form.title} onChange={(e) => set("title", e.target.value)} className="input" placeholder="Practice guitar" />
        </label>

        <label className="md:col-span-2">
          <span className="label">Description</span>
          <textarea value={form.description} onChange={(e) => set("description", e.target.value)} className="input min-h-24 resize-y" placeholder="What needs to be done?" />
        </label>

        <div className="md:col-span-2">
          <span className="label">Type</span>
          <div className="grid grid-cols-2 gap-3 sm:grid-cols-4" role="group" aria-label="Task type">
            {types.map(([value, label, icon, description]) => (
              <button
                type="button"
                key={value}
                onClick={() => set("type", value)}
                aria-pressed={form.type === value}
                className={`flex min-h-28 flex-col items-start rounded-2xl border p-3 text-left transition focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-500 ${
                  form.type === value
                    ? "border-blue-500 bg-blue-50 text-blue-800 shadow-sm dark:bg-blue-500/10 dark:text-blue-200"
                    : "border-slate-200 bg-white text-slate-700 hover:border-slate-300 hover:bg-slate-50 dark:border-slate-700 dark:bg-slate-900 dark:text-slate-200 dark:hover:border-slate-600 dark:hover:bg-slate-800"
                }`}
              >
                <span className={`mb-2 grid h-8 w-8 place-items-center rounded-lg text-base ${
                  form.type === value
                    ? "bg-blue-100 text-blue-700 dark:bg-blue-500/20 dark:text-blue-200"
                    : "bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300"
                }`} aria-hidden="true">
                  {icon}
                </span>
                <span className="text-sm font-semibold">{label}</span>
                <span className="mt-1 text-xs leading-4 text-slate-500 dark:text-slate-400">
                  {description}
                </span>
              </button>
            ))}
          </div>
        </div>

        <label>
          <span className="label">Priority: {form.priority}/5</span>
          <input type="range" min="1" max="5" value={form.priority} onChange={(e) => set("priority", e.target.value)} className="mt-3 w-full accent-blue-600" />
        </label>

        <label>
          <span className="label">Goal</span>
          <select value={form.goalId ?? ""} onChange={(e) => set("goalId", e.target.value || null)} className="input">
            <option value="">No goal</option>
            {goals.map((goal) => <option key={goal.id} value={goal.id}>{goal.title}</option>)}
          </select>
        </label>

        <label>
          <span className="label">Due date</span>
          <input type="date" value={form.dueDate} onChange={(e) => set("dueDate", e.target.value)} className="input" />
        </label>

        <label>
          <span className="label">Due time</span>
          <input type="time" value={form.dueTime} onChange={(e) => set("dueTime", e.target.value)} className="input" />
        </label>

        <label>
          <span className="label">Expected duration (minutes)</span>
          <input type="number" min="1" value={form.expectedDurationMinutes} onChange={(e) => set("expectedDurationMinutes", e.target.value)} className="input" />
        </label>

        <label>
          <span className="label">Reminder date/time</span>
          <input type="datetime-local" value={form.reminderDateTime} onChange={(e) => set("reminderDateTime", e.target.value)} className="input" />
        </label>

        <label>
          <span className="label">Recurrence</span>
          <select value={form.recurrence.type} onChange={(e) => setRecurrence({ type: e.target.value })} className="input">
            <option value="NONE">None</option>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="CUSTOM_DAYS">Custom days</option>
            <option value="FOR_N_DAYS">For N days</option>
          </select>
        </label>

        {form.recurrence.type === "CUSTOM_DAYS" && (
          <div className="md:col-span-2">
            <span className="label">Days of week</span>
            <div className="flex flex-wrap gap-2">
              {days.map((day) => (
                <button type="button" key={day} onClick={() => toggleDay(day)} className={`rounded-lg px-3 py-2 text-xs font-semibold ${form.recurrence.daysOfWeek?.includes(day) ? "bg-blue-600 text-white" : "bg-slate-100 text-slate-600 dark:bg-slate-800 dark:text-slate-300"}`}>
                  {day}
                </button>
              ))}
            </div>
          </div>
        )}

        {form.recurrence.type === "FOR_N_DAYS" && (
          <label>
            <span className="label">Occurrences</span>
            <input type="number" min="1" value={form.recurrence.occurrences || ""} onChange={(e) => setRecurrence({ occurrences: e.target.value })} className="input" />
          </label>
        )}

        {form.recurrence.type !== "NONE" && (
          <label>
            <span className="label">Optional end date</span>
            <input type="date" value={form.recurrence.endDate || ""} onChange={(e) => setRecurrence({ endDate: e.target.value })} className="input" />
          </label>
        )}
      </div>

      <div className="mt-6 flex justify-end gap-2">
        <button type="button" onClick={onCancel} className="button-secondary">Cancel</button>
        <button type="submit" className="button-primary">{initialValues ? "Save changes" : "Create task"}</button>
      </div>
    </form>
  );
}