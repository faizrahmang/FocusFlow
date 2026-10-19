import { useState, useMemo } from "react";
import { useAuth } from "./features/auth/useAuth";
import { useTasks } from "./features/tasks/hooks/useTasks";
import { useGoals } from "./features/goals/hooks/useGoals";
import { useAnalytics } from "./features/analytics/hooks/useAnalytics";
import { useChatbot } from "./features/chatbot/hooks/useChatbot";
import { useDarkMode } from "./shared/hooks/useDarkMode";

// Part C components
import GoogleLoginButton from "./features/auth/components/GoogleLoginButton";
import TaskList from "./features/tasks/components/TaskList";
import CompletionHistory from "./features/tasks/components/CompletionHistory";
import TaskForm from "./features/tasks/components/TaskForm";
import CompleteTaskModal from "./features/tasks/components/CompleteTaskModal";
import ProgressBarChart from "./features/analytics/components/ProgressBarChart";
import CompletionPieChart from "./features/analytics/components/CompletionPieChart";
import SummaryCards from "./features/analytics/components/SummaryCards";
import RangeToggle from "./features/analytics/components/RangeToggle";
import ChatbotPanel from "./features/chatbot/components/ChatbotPanel";
import DarkModeToggle from "./shared/components/DarkModeToggle";
import DueSoonBanner from "./shared/components/DueSoonBanner";
import GoalList from "./features/goals/components/GoalList";

export default function App() {
  const {
    user,
    isAuthenticated,
    loading: authLoading,
    error: authError,
    login,
    logout,
  } = useAuth();
  const { isDark, toggle: toggleDarkMode } = useDarkMode();

  const {
    tasks,
    loading: tasksLoading,
    error: tasksError,
    createTask,
    updateTask,
    deleteTask,
    reorderTasks,
    completeTask,
    dueSoonTasks,
    completionHistory,
    historyLoading,
    historyError,
    refetch: refetchTasks,
  } = useTasks({ archived: false });

  const { goals } = useGoals();
  const { summary, timeline, range, setRange, loading: analyticsLoading, error: analyticsError } = useAnalytics();
  const { messages, loading: chatLoading, error: chatError, sendMessage, activeTaskId, setActiveTaskId } = useChatbot();

  // Derive the active task title for the ChatbotPanel display
  const activeTaskTitle = useMemo(() => {
    if (!activeTaskId) return null;
    const found = tasks.find((t) => t.id === activeTaskId);
    return found ? found.title : `Task #${activeTaskId}`;
  }, [activeTaskId, tasks]);

  // Local UI state
  const [taskFormOpen, setTaskFormOpen] = useState(false);
  const [editingTask, setEditingTask] = useState(null);
  const [completingTask, setCompletingTask] = useState(null);

  const openCreateTask = () => {
    setEditingTask(null);
    setTaskFormOpen(true);
  };
  const openEditTask = (task) => {
    setEditingTask(task);
    setTaskFormOpen(true);
  };

  const handleTaskFormSubmit = async (taskRequest) => {
    if (editingTask) {
      await updateTask(editingTask.id, taskRequest);
    } else {
      await createTask(taskRequest);
    }
    setTaskFormOpen(false);
  };

  const handleCompleteSubmit = async ({ actualDurationMinutes, note, photoFile }) => {
    if (!completingTask) return;
    await completeTask(completingTask.id, { actualDurationMinutes, note, photoFile });
    setCompletingTask(null);
  };

  if (authLoading) {
    return <main className="auth-loading" role="status">Loading FocusFlow…</main>;
  }

  if (!isAuthenticated) {
    return (
      <main className="auth-shell">
        <section className="auth-story" aria-labelledby="auth-title">
          <header className="auth-story-header">
            <div className="brand-lockup">
              <span className="brand-mark" aria-hidden="true">F</span>
              <span>FocusFlow</span>
            </div>
            <DarkModeToggle isDark={isDark} onToggle={toggleDarkMode} />
          </header>

          <div className="story-copy">
            <p className="eyebrow">A calmer way to get things done</p>
            <h1 id="auth-title">Your next win starts here.</h1>
            <p>
              Turn busy days into clear, achievable progress with a todo space
              that adapts to the way you work.
            </p>
          </div>

          <div className="story-proof" aria-label="FocusFlow benefits">
            <div>
              <strong>Plan</strong>
              <span>Turn ideas into focused steps.</span>
            </div>
            <div>
              <strong>Track</strong>
              <span>See momentum build every day.</span>
            </div>
            <div>
              <strong>Reflect</strong>
              <span>Learn what helps you move forward.</span>
            </div>
          </div>
        </section>

        <section className="auth-panel" aria-labelledby="signin-title">
          <div className="auth-card">
            <div className="auth-card-heading">
              <p className="eyebrow">Welcome back</p>
              <h2 id="signin-title">Sign in to FocusFlow</h2>
              <p>Pick up where you left off and make today count.</p>
            </div>

            {authError && (
              <p className="auth-error" role="alert">
                {authError.message || String(authError)}
              </p>
            )}

            <div className="auth-alternative" aria-label="Sign in options">
              <span aria-hidden="true" />
              <p>Get started securely</p>
              <span aria-hidden="true" />
            </div>
            <GoogleLoginButton onClick={login} />
            <p className="auth-note">
              New to FocusFlow? Your account will be created when you continue
              with Google.
            </p>
          </div>
        </section>
      </main>
    );
  }

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 dark:bg-[#080d18] dark:text-slate-100">
      <header className="sticky top-0 z-30 border-b border-slate-200/80 bg-white/85 backdrop-blur-xl dark:border-slate-800/80 dark:bg-[#080d18]/85">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3.5 sm:px-6 lg:px-8">
          <div className="flex items-center gap-3">
            <div className="grid h-10 w-10 place-items-center rounded-2xl bg-blue-600 font-bold text-white shadow-lg shadow-blue-600/20">F</div>
            <div>
              <h1 className="font-bold tracking-tight">FocusFlow</h1>
              <p className="hidden text-xs text-slate-500 dark:text-slate-400 sm:block">Tasks · Goals · Progress</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <span className="hidden max-w-40 truncate text-sm font-medium text-slate-600 dark:text-slate-300 sm:block">{user?.name}</span>
            {user?.avatarUrl && <img src={user.avatarUrl} alt="" className="h-9 w-9 rounded-full ring-2 ring-white dark:ring-slate-800" />}
            <DarkModeToggle isDark={isDark} onToggle={toggleDarkMode} />
            <button onClick={logout} className="rounded-xl px-3 py-2 text-sm font-medium text-slate-600 transition hover:bg-slate-100 hover:text-slate-950 focus-visible:outline focus-visible:outline-2 focus-visible:outline-blue-500 dark:text-slate-300 dark:hover:bg-slate-800 dark:hover:text-white">Log out</button>
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-7xl space-y-10 px-4 py-7 sm:px-6 sm:py-9 lg:px-8">
        <DueSoonBanner tasks={dueSoonTasks} onDismiss={refetchTasks} />

        <section className="relative isolate overflow-hidden rounded-[2rem] border border-slate-200/80 bg-gradient-to-br from-white via-white to-blue-50 p-6 shadow-sm dark:border-slate-800 dark:from-slate-900 dark:via-slate-900 dark:to-slate-900 sm:p-9">
          <div className="pointer-events-none absolute -right-16 -top-24 -z-10 h-72 w-72 rounded-full bg-blue-200/40 blur-3xl dark:bg-blue-500/10" />
          <div className="flex flex-col gap-7 sm:flex-row sm:items-end sm:justify-between">
            <div className="max-w-2xl">
              <p className="inline-flex items-center gap-2 text-sm font-semibold text-blue-700 dark:text-blue-300">
                <span className="h-2 w-2 rounded-full bg-blue-500" />
                Good evening
              </p>
              <h2 className="mt-3 text-3xl font-bold tracking-tight text-slate-950 dark:text-white sm:text-4xl">
                Stay consistent. <span className="text-slate-500 dark:text-slate-400">Make progress.</span>
              </h2>
              <p className="mt-3 max-w-xl text-sm leading-6 text-slate-600 dark:text-slate-400 sm:text-base">
                A little focus goes a long way. Choose what matters and keep your momentum going.
              </p>
            </div>
            <button onClick={openCreateTask} className="inline-flex min-h-11 shrink-0 items-center justify-center gap-2 self-start rounded-xl bg-blue-600 px-5 text-sm font-semibold text-white shadow-lg shadow-blue-600/20 transition hover:bg-blue-700 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-blue-500 sm:self-auto">
              <span aria-hidden="true" className="text-lg leading-none">+</span>
              New task
            </button>
          </div>
        </section>

        {tasksError && <p role="alert" className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700 dark:border-red-900/60 dark:bg-red-950/40 dark:text-red-300">{tasksError}</p>}
        {tasksLoading ? (
          <p className="text-sm text-slate-500">Loading tasks…</p>
        ) : (
          <TaskList
            tasks={tasks}
            onComplete={setCompletingTask}
            onArchive={(id) => console.log("archive task", id)}
            onDelete={deleteTask}
            onReorder={reorderTasks}
            onSelect={openEditTask}
          />
        )}

        <CompletionHistory items={completionHistory} loading={historyLoading} error={historyError} />

        <section className="space-y-5">
          <div>
            <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-slate-400">Long-term direction</p>
            <h3 className="mt-1 text-xl font-bold tracking-tight">My goals</h3>
          </div>
          <GoalList goals={goals} />
        </section>

        <section className="space-y-5">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-xs font-semibold uppercase tracking-[0.16em] text-slate-500 dark:text-slate-400">Your momentum</p>
              <h3 className="mt-1 text-xl font-bold tracking-tight">Progress &amp; analytics</h3>
            </div>
            <RangeToggle selected={range} onSelect={setRange} />
          </div>
          {analyticsError && <p className="text-red-500 text-sm">{analyticsError}</p>}
          {analyticsLoading ? (
            <p className="text-sm text-slate-500">Loading analytics…</p>
          ) : (
            <>
              {summary && <SummaryCards summary={summary} />}
              <div className="grid gap-5 lg:grid-cols-[minmax(0,1.5fr)_minmax(280px,1fr)]">
                <div className="rounded-2xl border border-slate-200 bg-white p-5 dark:border-slate-800 dark:bg-slate-900">
                  <h4 className="font-semibold">Progress over time</h4>
                  <ProgressBarChart data={timeline} />
                </div>
                <div className="rounded-2xl border border-slate-200 bg-white p-5 dark:border-slate-800 dark:bg-slate-900">
                  <h4 className="font-semibold">Completion by type</h4>
                  {summary && <CompletionPieChart data={summary.byType || {}} />}
                </div>
              </div>
            </>
          )}
        </section>

        <ChatbotPanel
          messages={messages}
          loading={chatLoading}
          error={chatError}
          onSend={sendMessage}
          activeTaskTitle={activeTaskTitle}
        />
      </main>

      {/* Task form overlay */}
      {taskFormOpen && (
        <div className="fixed inset-0 z-50 grid place-items-center bg-slate-950/60 p-4">
          <div className="w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <TaskForm
              initialValues={editingTask}
              goals={goals}
              onSubmit={handleTaskFormSubmit}
              onCancel={() => setTaskFormOpen(false)}
            />
          </div>
        </div>
      )}

      {/* Complete task modal */}
      {completingTask && (
        <CompleteTaskModal
          task={completingTask}
          onSubmit={handleCompleteSubmit}
          onCancel={() => setCompletingTask(null)}
        />
      )}
    </div>
  );
}
