import { Component, EventEmitter, Input, Output, signal } from '@angular/core';
import { ReactiveFormsModule, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe } from '@ngx-translate/core';
import { UserProfileForm } from '../../../../core/models/user.model';
import { AVAILABLE_LOCATIONS } from '../../../../core/models/location.model';
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
    UserProfileView,
  ],
  templateUrl: './user-profile.view.html',
})
export class UserProfileView {
  @Input() posterName: string | null = null;
  @Input() posterPreviewUrl: string | null = null;
  @Input({ required: true }) formGroup!: FormGroup<UserProfileForm>;
  @Input() isLoading = false;

  @Output() cancelEvent = new EventEmitter<void>();
  @Output() posterSelectedEvent = new EventEmitter<File>();
  @Output() submitEvent = new EventEmitter<void>();
  @Output() invalidSubmit = new EventEmitter<void>();

  readonly locations = AVAILABLE_LOCATIONS;
  onPosterSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (file) {
      this.posterSelectedEvent.emit(file);
    }

    input.value = '';
  }
}
