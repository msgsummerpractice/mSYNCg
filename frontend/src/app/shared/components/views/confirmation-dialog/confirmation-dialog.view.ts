import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { TranslateService } from '@ngx-translate/core';

export interface ConfirmationDialogData {
  message: string;
}

@Component({
  selector: 'app-confirmation-dialog-view',
  imports: [MatDialogModule, MatButtonModule],
  template: `
    <h2 mat-dialog-title>{{ titleLabel }}</h2>
    <mat-dialog-content>{{ data.message }}</mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button type="button" (click)="close(false)">{{ noButtonLabel }}</button>
      <button mat-flat-button type="button" color="primary" (click)="close(true)">{{
        yesButtonLabel
      }}</button>
    </mat-dialog-actions>
  `,
})
export class ConfirmationDialogView {
  private readonly translate = inject(TranslateService);
  yesButtonLabel = this.translate.instant('CONFIRMATION_DIALOG.CONFIRM_BUTTON');
  noButtonLabel = this.translate.instant('CONFIRMATION_DIALOG.CANCEL_BUTTON');
  titleLabel = this.translate.instant('CONFIRMATION_DIALOG.TITLE');
  readonly data = inject<ConfirmationDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<ConfirmationDialogView, boolean>);

  close(confirmed: boolean): void {
    this.dialogRef.close(confirmed);
  }
}
