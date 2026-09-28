const listeners = new Set();

export function onSessionEnd(cb) {
  listeners.add(cb);
  return () => listeners.delete(cb);
}

export function emitSessionEnd() {
  listeners.forEach((cb) => cb());
}
