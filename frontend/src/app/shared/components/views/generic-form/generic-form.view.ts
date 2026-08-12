import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';

@Component({
    selector: 'app-generic-form-view',
    standalone: true,
    imports: [ReactiveFormsModule, MatButtonModule],
    templateUrl: './generic-form.view.html',
})
export class GenericFormView {
    @Input({ required: true }) formGroup!: FormGroup;
    @Input() submitLabel: string = '';
    @Input() isLoading: boolean = false;

    @Output() formSubmit = new EventEmitter<void>();

    onSubmit(): void {
        if (this.formGroup.valid) {
            this.formSubmit.emit();
        } else {
            this.formGroup.markAllAsTouched();
        }
    }
}