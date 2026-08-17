import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const dateRangeValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  const startDate = control.get('startDate')?.value as Date | null;
  const startTime = control.get('startTime')?.value as Date | null;
  const endDate = control.get('endDate')?.value as Date | null;
  const endTime = control.get('endTime')?.value as Date | null;

  if (!startDate || !startTime || !endDate || !endTime) {
    return null;
  }

  const start = new Date(startDate);
  start.setHours(startTime.getHours(), startTime.getMinutes(), 0, 0);

  const end = new Date(endDate);
  end.setHours(endTime.getHours(), endTime.getMinutes(), 0, 0);

  return end < start ? { invalidDateRange: true } : null;
};
