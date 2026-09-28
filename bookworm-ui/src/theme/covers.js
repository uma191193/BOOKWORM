// Deterministic gradient per book ID for beautiful cover placeholders
const GRADIENTS = [
  ['#667eea', '#764ba2'],
  ['#f093fb', '#f5576c'],
  ['#4facfe', '#00f2fe'],
  ['#43e97b', '#38f9d7'],
  ['#fa709a', '#fee140'],
  ['#a18cd1', '#fbc2eb'],
  ['#fccb90', '#d57eeb'],
  ['#a1c4fd', '#c2e9fb'],
  ['#fd7043', '#ff8a65'],
  ['#26c6da', '#00acc1'],
];

export function coverGradient(id) {
  if (!id) return GRADIENTS[0];
  const idx = parseInt(id.replace(/-/g, '').slice(0, 8), 16) % GRADIENTS.length;
  return GRADIENTS[Math.abs(idx)];
}
