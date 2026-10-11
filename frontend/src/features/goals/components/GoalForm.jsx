import { useEffect, useState } from "react";

const empty = { title: "", type: "TASK", targetDate: "", linkedTaskIds: [] };
const types = ["HABIT", "TASK", "SKILL", "HEALTH"];

export default function GoalForm({ initialValues, tasks = [], onSubmit, onCancel }) {
  const [form, setForm] = useState(initialValues || empty);

  useEffect(() => setForm(initialValues || empty), [initialValues]);

  const toggleTask = (id) => {
    setForm((current) => ({
      ...current,
      linkedTaskIds: current.linkedTaskIds.includes(id)
        ? current.linkedTaskIds.filter((taskId) => taskId !== id)
        : [...current.linkedTaskIds, id],
    }));
  };

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSubmit?.(form); }} className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm dark:border-slate-800 dark:bg-slate-900">
      <h2 className="text-lg font-bold text-slate-900 dark:text-white">{initialValues ? "Edit goal" : "Create goal"}</h2>

      <div className="mt-5 space-y-5">
        <label>
          <span className="label">Title</span>
          <input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} className="input" placeholder="Learn guitar basics" />
        </label>

        <label>
          <span className="label">Type</span>
          <select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })} className="input">
            {types.map((type) => <option key={type}>{type}</option>)}
          </select>
        </label>

        <label>
          <span className="label">Target date</span>
          <input type="date" value={form.targetDate || ""} onChange={(e) => setForm({ ...form, targetDate: e.target.value })} className="input" />
        </label>

        <fieldset>
          <legend className="label">Linked tasks</legend>
          <div className="max-h-48 space-y-2 overflow-auto rounded-xl border border-slate-200 p-3 dark:border-slate-700">
            {tasks.length === 0 ? <p className="text-sm text-slate-400">No tasks available.</p> : tasks.map((task) => (
              <label key={task.id} className="flex items-center gap-3 rounded-lg p-2 hover:bg-slate-50 dark:hover:bg-slate-800">
                <input type="checkbox" checked={form.linkedTaskIds.includes(task.id)} onChange={() => toggleTask(task.id)} />
                <span className="text-sm text-slate-700 dark:text-slate-200">{task.title}</span>
              </label>
            ))}
          </div>
        </fieldset>
      </div>

      <div className="mt-6 flex justify-end gap-2">
        <button type="button" onClick={onCancel} className="button-secondary">Cancel</button>
        <button type="submit" className="button-primary">{initialValues ? "Save changes" : "Create goal"}</button>
      </div>
    </form>
  );
}