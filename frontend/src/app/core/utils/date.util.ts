export function combineDateTime(date: Date | null, time: Date | null): Date | null {
  if (date === null || time === null) {
    return null;
  }

  const combined = new Date(date);
  combined.setHours(time.getHours(), time.getMinutes(), 0, 0);

  return combined;
}

export function formatDateTime(value: Date | null): string {
  if (value === null) {
    return '';
  }

  // Convert to UTC ISO 8601 string
  return new Date(value).toISOString();
}

export function parseDateTime(value: string | null | undefined): Date | null {
  if (!value) {
    return null;
  }

  const parsed = new Date(value);

  return Number.isNaN(parsed.getTime()) ? null : parsed;
}

export function formatDate(value: Date | null): string {
  if (value === null) {
    return '';
  }

  const year = value.getFullYear();
  const month = String(value.getMonth() + 1).padStart(2, '0');
  const day = String(value.getDate()).padStart(2, '0');

  return `${year}-${month}-${day}`;
}
