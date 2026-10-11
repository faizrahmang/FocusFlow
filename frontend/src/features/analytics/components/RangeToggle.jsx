const ranges = ["daily", "weekly", "monthly"];

export default function RangeToggle({ selected, onSelect }) {
  return (
    <div className="inline-flex rounded-xl bg-slate-100 p-1 dark:bg-slate-800">
      {ranges.map((range) => (
        <button key={range} onClick={() => onSelect?.(range)} className={`rounded-lg px-3 py-1.5 text-xs font-semibold capitalize ${selected === range ? "bg-white text-slate-900 shadow-sm dark:bg-slate-700 dark:text-white" : "text-slate-500"}`}>
          {range}
        </button>
      ))}
    </div>
  );
}