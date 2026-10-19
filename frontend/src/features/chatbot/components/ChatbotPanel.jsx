import { useState } from "react";

export default function ChatbotPanel({ messages = [], onSend, activeTaskTitle = null, loading = false, error = null }) {
  const [text, setText] = useState("");

  const send = async (event) => {
    event.preventDefault();
    if (!text.trim() || loading) return;
    const result = await onSend?.(text.trim());
    if (result !== null) setText("");
  };

  return (
    <aside className="flex h-[min(720px,calc(100vh-6rem))] w-full flex-col overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-xl dark:border-slate-800 dark:bg-slate-900">
      <header className="border-b border-slate-100 px-5 py-4 dark:border-slate-800">
        <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">AI Coach</p>
        <h2 className="mt-1 font-bold text-slate-900 dark:text-white">
          {activeTaskTitle ? `Chatting about: ${activeTaskTitle}` : "General coaching chat"}
        </h2>
      </header>

      <div className="flex-1 space-y-4 overflow-y-auto p-4">
        {messages.length === 0 && (
          <div className="mt-12 text-center">
            <div className="text-3xl">✦</div>
            <p className="mt-3 text-sm font-medium text-slate-700 dark:text-slate-200">How can I help?</p>
            <p className="mt-1 text-xs text-slate-400">Ask about your task, habit, or learning goal.</p>
          </div>
        )}

        {messages.map((message) => {
          const user = message.role === "user";
          return (
            <div key={message.id} className={`flex ${user ? "justify-end" : "justify-start"}`}>
              <div className={`max-w-[82%] rounded-2xl px-4 py-3 text-sm ${user ? "rounded-br-md bg-blue-600 text-white" : "rounded-bl-md bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-200"}`}>
                {message.content}
              </div>
            </div>
          );
        })}

        {loading && <div className="text-xs text-slate-400">Coach is thinking…</div>}
        {error && (
          <p role="alert" className="rounded-xl border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700 dark:border-red-900/60 dark:bg-red-950/40 dark:text-red-300">
            {error}
          </p>
        )}
      </div>

      <form onSubmit={send} className="border-t border-slate-100 p-3 dark:border-slate-800">
        <div className="flex gap-2 rounded-2xl bg-slate-100 p-1.5 dark:bg-slate-800">
          <input value={text} onChange={(e) => setText(e.target.value)} className="min-w-0 flex-1 bg-transparent px-3 py-2 text-sm outline-none" placeholder="Ask your coach…" />
          <button disabled={!text.trim() || loading} className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-semibold text-white disabled:opacity-40 dark:bg-white dark:text-slate-900">Send</button>
        </div>
      </form>
    </aside>
  );
}