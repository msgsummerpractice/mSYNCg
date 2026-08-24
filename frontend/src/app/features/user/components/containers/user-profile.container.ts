import { Component, inject, signal } from '@angular/core';
import { User, UserProfileForm } from '../../../../core/models/user.model';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslatePipe, TranslateService } from '@ngx-translate/core';
import { UserProfileView } from '../views/user-profile.view';
import { Router } from '@angular/router';
import { ToastService } from '../../../../core/services/toast.service';
import { UserRole } from '../../../../core/constants/role.constant';
import { UserLocation } from '../../../../core/constants/location.constant';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-user-profile-container',
  imports: [
    TranslatePipe,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    ReactiveFormsModule,
    UserProfileView,
    UserProfileContainer,
  ],
  template: `
    <app-user-profile-view
      (cancelEvent)="handleCancel()"
      [formGroup]="userProfileGroup"
      (submitEvent)="handleEventSubmit()"
      (invalidSubmit)="handleInvalidForm()"
      [posterName]="posterName()"
      [posterPreviewUrl]="posterPreviewUrl()"
      [isLoading]="isLoading()"
      (posterSelectedEvent)="handlePosterSelected($event)"
    ></app-user-profile-view>
  `,
})
export class UserProfileContainer {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly router = inject(Router);
  private readonly toastService = inject(ToastService);
  private readonly translate = inject(TranslateService);
  private readonly authService = inject(AuthService);
  readonly posterName = signal<string | null>(null);
  readonly posterPreviewUrl = signal<string | null>(null);
  protected posterBase64: string | null = null;
  readonly isLoading = signal<boolean>(false);

  protected readonly userProfileGroup = this.fb.group<UserProfileForm>({
    firstName: this.fb.control(null),
    lastName: this.fb.control(null),
    email: this.fb.control(null),
    location: this.fb.control(null),
    role: this.fb.control(null),
    posterName: this.fb.control(null),
  });

  ngOnInit(): void {
    this.authService.loadCurrentUser().subscribe((user) => {
      if (!user) {
        return;
      }

      this.userProfileGroup.patchValue({
        firstName: user.firstName,
        lastName: user.lastName,
        email: user.email,
        role: user.role,
        location: user.location,
      });
    });
  }

  handleCancel(): void {
    this.router.navigate(['/profile-page']);
  }

  handlePosterSelected(file: File): void {
    if (!this.isValidPoster(file)) {
      this.toastService.showError(this.translate.instant('REGISTER.EVENT.POSTER.INVALID'));
      return;
    }

    const reader = new FileReader();

    reader.onload = () => {
      const result = reader.result!.toString();
      this.posterBase64 = result.split(',')[1] ?? result;
      this.posterName.set(file.name);
      this.posterPreviewUrl.set(result);
    };

    reader.readAsDataURL(file);
  }

  private isValidPoster(file: File): boolean {
    const acceptedTypes = ['image/jpeg', 'image/png'];
    const maxSizeInBytes = 5 * 1024 * 1024;

    return acceptedTypes.includes(file.type) && file.size <= maxSizeInBytes;
  }

  handleInvalidForm(): void {
    this.toastService.showError(this.translate.instant('REGISTER.EVENT.FORM.INVALID'));
  }

  handleEventSubmit(): void {}
}
