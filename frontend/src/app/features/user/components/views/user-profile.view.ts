import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslatePipe } from '@ngx-translate/core';
import { UserProfileForm } from '../../../../core/models/user.model';
import { UserLocation } from '../../../../core/constants/location.constant';
import { GenericFormContainer } from '../../../../shared/components/containers/generic-form.container';

@Component({
  selector: 'app-user-profile-view',
  imports: [
    TranslatePipe,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    ReactiveFormsModule,
    GenericFormContainer,
    MatProgressSpinnerModule,
  ],
  templateUrl: './user-profile.view.html',
})
export class UserProfileView {
  @Input() posterName: string | null = null;
  @Input() posterPreviewUrl: string | null = null;
  @Input({ required: true }) formGroup!: FormGroup<UserProfileForm>;
  @Input() isLoading = false;
  @Input() isDataLoading = false;

  @Output() cancelEvent = new EventEmitter<void>();
  @Output() posterSelectedEvent = new EventEmitter<File>();
  @Output() submitEvent = new EventEmitter<void>();
  @Output() invalidSubmit = new EventEmitter<void>();

  readonly locations = Object.values(UserLocation);
  onPosterSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      this.posterSelectedEvent.emit(file);
    }

    input.value = '';
  }
}
