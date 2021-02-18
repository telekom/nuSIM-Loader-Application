export default function(instant) {
  const d = new Date(instant.epochSecond * 1000)
  return d.toLocaleString()
}
