import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const eventDateTimeRangeValidator: ValidatorFn = (
  control: AbstractControl
): ValidationErrors | null => {
  const startDate = control.get('startDate')?.value as Date | null;
  const startTime = control.get('startTime')?.value as Date | null;
  const endDate = control.get('endDate')?.value as Date | null;
  const endTime = control.get('endTime')?.value as Date | null;
  const registrationStartDate = control.get('registrationStartDate')?.value as Date | null;
  const registrationStartTime = control.get('registrationStartTime')?.value as Date | null;
  const registrationEndDate = control.get('registrationEndDate')?.value as Date | null;
  const registrationEndTime = control.get('registrationEndTime')?.value as Date | null;

  const errors: ValidationErrors = {};

  if (isDateInPast(startDate, startTime)) {
    errors['startDateInPast'] = true;
  }

  if (isDateInPast(registrationStartDate, registrationStartTime)) {
    errors['registrationStartDateInPast'] = true;
  }

  if (isInvalidRange(startDate, startTime, endDate, endTime)) {
    errors['invalidDateRange'] = true;
  }

  if (
    isInvalidRange(
      registrationStartDate,
      registrationStartTime,
      registrationEndDate,
      registrationEndTime
    )
  ) {
    errors['invalidRegistrationDateRange'] = true;
  }

  return Object.keys(errors).length > 0 ? errors : null;
};

function isDateInPast(date: Date | null, time: Date | null): boolean {
  if (!date) {
    return false;
  }

  const now = new Date();
  const checkDate = new Date(date);
  checkDate.setHours(0, 0, 0, 0);

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  if (checkDate.getTime() < today.getTime()) {
    return true;
  }

  if (checkDate.getTime() > today.getTime() || !time) {
    return false;
  }

  const fullDateTime = new Date(checkDate);
  fullDateTime.setHours(time.getHours(), time.getMinutes(), 0, 0);

  return fullDateTime.getTime() < now.getTime();
}

function isInvalidRange(
  startDate: Date | null,
  startTime: Date | null,
  endDate: Date | null,
  endTime: Date | null
): boolean {
  if (!startDate || !endDate) {
    return false;
  }

  const start = new Date(startDate);
  start.setHours(0, 0, 0, 0);

  const end = new Date(endDate);
  end.setHours(0, 0, 0, 0);

  if (end.getTime() < start.getTime()) {
    return true;
  }

  if (end.getTime() > start.getTime() || !startTime || !endTime) {
    return false;
  }

  const fullStart = new Date(start);
  fullStart.setHours(startTime.getHours(), startTime.getMinutes(), 0, 0);

  const fullEnd = new Date(end);
  fullEnd.setHours(endTime.getHours(), endTime.getMinutes(), 0, 0);

  return fullEnd.getTime() < fullStart.getTime();
}
