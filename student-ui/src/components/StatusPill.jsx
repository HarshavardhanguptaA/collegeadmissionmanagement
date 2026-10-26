export default function StatusPill({ status }) {
  const tone = String(status || "SUBMITTED").toLowerCase().replaceAll("_", "-");
  return <span className={`status status-${tone}`}>{String(status || "SUBMITTED").replaceAll("_", " ")}</span>;
}
