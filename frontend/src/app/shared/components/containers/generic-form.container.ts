import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { GenericFormView } from '../views/generic-form/generic-form.view';

@Component({
  selector: 'app-generic-form-container',
  standalone: true,
  imports: [CommonModule, GenericFormView],
  template: `
    <app-generic-form-view
      [formGroup]="formGroup"
      [isLoading]="isLoading"
      [submitLabel]="submitLabel$ | async"
      (formSubmit)="onFormSubmit()"
      (invalidSubmit)="invalidSubmit.emit()"
    >
      <ng-content></ng-content>
    </app-generic-form-view>
  `,
})
export class GenericFormContainer {
  @Input({ required: true }) formGroup!: FormGroup;
  @Input() isLoading: boolean = false;

  @Output() formSubmit = new EventEmitter<void>();
  @Output() invalidSubmit = new EventEmitter<void>();

  submitLabel$: Observable<string>;

  constructor(private translate: TranslateService) {
    this.submitLabel$ = this.translate.stream('BUTTONS.SUBMIT');
  }

  onFormSubmit(): void {
    this.formSubmit.emit();
  }
}
