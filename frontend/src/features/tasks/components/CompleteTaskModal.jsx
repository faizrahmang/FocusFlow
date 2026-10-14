import { useState } from "react";

export default function CompleteTaskModal({ task, onSubmit, onCancel }) {
  const [actualDurationMinutes, setActualDurationMinutes] = useState(task?.expectedDurationMinutes || "");
  const [note, setNote] = useState("");
  const [photoFile, setPhotoFile] = useState(null);
  const [error, setError] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  const submit = async (event) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await onSubmit?.({
        actualDurationMinutes: actualDurationMinutes ? Number(actualDurationMinutes) : null,
        note: note || null,
        photoFile,
      });
    } catch (err) {
      // Keep the modal open so the user can retry (e.g. pick a smaller photo)
      setError(err?.message || "Could not mark this task complete. Please try again.");
    } finally {
      setSubmitting(false);
    }
  };

  if (!task) return null;

  return (
    <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/60 p-4 backdrop-blur-sm">
      <form onSubmit={submit} className="w-full max-w-lg max-h-[90vh] overflow-y-auto rounded-3xl bg-white p-6 shadow-2xl dark:bg-slate-900">
        <div className="flex items-start justify-between">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-emerald-600">Complete task</p>
            <h2 className="mt-1 text-xl font-bold text-slate-900 dark:text-white">{task.title}</h2>
          </div>
          <button type="button" onClick={onCancel} className="rounded-full p-2 text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800">✕</button>
        </div>

        <div className="mt-5 rounded-2xl bg-slate-50 p-4 dark:bg-slate-800/70">
          <div className="flex justify-between text-sm">
            <span className="text-slate-500">Expected duration</span>
            <strong className="text-slate-800 dark:text-slate-100">{task.expectedDurationMinutes ?? "—"} min</strong>
          </div>
        </div>

        <div className="mt-5 space-y-4">
          <label>
            <span className="label">Actual duration (minutes)</span>
            <input type="number" min="0" value={actualDurationMinutes} onChange={(e) => setActualDurationMinutes(e.target.value)} className="input" />
          </label>

          <label>
            <span className="label">Note</span>
            <textarea value={note} onChange={(e) => setNote(e.target.value)} className="input min-h-24 resize-y" placeholder="How did it go?" />
          </label>

          <label>
            <span className="label">Proof photo <span className="font-normal text-slate-400">(optional)</span></span>
            <input type="file" accept="image/*" capture="environment" onChange={(e) => setPhotoFile(e.target.files?.[0] || null)} className="mt-2 block w-full rounded-xl border border-dashed border-slate-300 p-3 text-sm dark:border-slate-700" />
          </label>
        </div>

        <div className="mt-6 flex justify-end gap-2">
          <button type="button" onClick={onCancel} className="button-secondary">Cancel</button>
          <button type="submit" disabled={submitting} className="button-primary disabled:opacity-60">
            {submitting ? "Saving…" : "Mark complete"}
          </button>
        </div>

        {error && (
          <p role="alert" className="mt-3 rounded-xl bg-rose-50 px-3 py-2 text-sm text-rose-700 dark:bg-rose-500/10 dark:text-rose-300">
            {error}
          </p>
        )}
      </form>
    </div>
  );
}