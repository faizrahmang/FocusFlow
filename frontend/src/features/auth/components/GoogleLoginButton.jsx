export default function GoogleLoginButton({ onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className="flex w-full items-center justify-center gap-3 rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm font-semibold text-slate-700 shadow-sm transition hover:bg-slate-50 dark:border-slate-600 dark:bg-slate-900 dark:text-slate-100 dark:hover:bg-slate-800"
    >
      <span className="grid h-5 w-5 place-items-center rounded-full border border-slate-300 text-xs font-bold dark:border-slate-500">
        G
      </span>

      Sign in with Google
    </button>
  );
}
