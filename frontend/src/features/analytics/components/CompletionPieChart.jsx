import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts";

const labels = { HABIT: "Habit", TASK: "Task", SKILL: "Skill", HEALTH: "Health" };
const dataKeys = ["HABIT", "TASK", "SKILL", "HEALTH"];

export default function CompletionPieChart({ data = {} }) {
  const chartData = dataKeys.map((key) => ({ name: labels[key], value: Number(data[key] || 0) })).filter((item) => item.value > 0);

  if (!chartData.length) {
    return <div className="grid h-72 place-items-center text-sm text-slate-400">No completion data yet.</div>;
  }

  return (
    <div className="h-72 w-full">
      <ResponsiveContainer>
        <PieChart>
          <Pie data={chartData} dataKey="value" nameKey="name" cx="50%" cy="50%" innerRadius={65} outerRadius={95} paddingAngle={3} isAnimationActive animationDuration={800}>
            {chartData.map((entry, index) => <Cell key={entry.name} fill={`hsl(${220 + index * 55} 75% ${55 - index * 4}%)`} />)}
          </Pie>
          <Tooltip />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}