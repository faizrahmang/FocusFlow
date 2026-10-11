import { Bar, BarChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";

export default function ProgressBarChart({ data = [] }) {
  return (
    <div className="h-72 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <BarChart data={data} margin={{ top: 10, right: 8, left: -20, bottom: 0 }}>
          <CartesianGrid vertical={false} strokeDasharray="3 3" />
          <XAxis dataKey="date" tick={{ fontSize: 11 }} tickFormatter={(value) => value.slice(5)} />
          <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
          <Tooltip />
          <Bar dataKey="completed" name="Completed" radius={[6, 6, 0, 0]} isAnimationActive animationDuration={700} />
          <Bar dataKey="total" name="Total" radius={[6, 6, 0, 0]} isAnimationActive animationDuration={900} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}