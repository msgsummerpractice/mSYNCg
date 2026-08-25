import { Component, DestroyRef, inject, signal } from '@angular/core';
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
import { AuthService } from '../../../../core/services/auth.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UserService } from '../../../../core/services/user.service';

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
  private readonly userService = inject(UserService);
  readonly posterPreviewUrl = signal<string | null>(null);
  private readonly destroyRef = inject(DestroyRef);
  protected posterBase64: string | null = null;
  readonly isLoading = signal<boolean>(false);

  protected readonly userProfileGroup = this.fb.group<UserProfileForm>({
    firstName: this.fb.control(null),
    lastName: this.fb.control(null),
    email: this.fb.control(null),
    location: this.fb.control(null),
    role: this.fb.control({ value: null, disabled: true }),
    posterName: this.fb.control(null),
  });

  ngOnInit(): void {
    const user = this.authService.currentUser();

    if (!user) {
      return;
    }

    this.userService
      .getUserProfile(user.id)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((profile) => {
        if (profile.imageBase64) {
          this.posterBase64 = profile.imageBase64;
          this.posterPreviewUrl.set(`data:${profile.imageMimeType};base64,${profile.imageBase64}`);
        }

        this.userProfileGroup.patchValue({
          firstName: profile.firstName,
          lastName: profile.lastName,
          email: profile.email,
          location: profile.location,
          role: profile.role,
        });
      });
  }

  handleCancel(): void {
    this.router.navigate(['/home']);
  }

  handlePosterSelected(file: File): void {
    if (!this.isValidPoster(file)) {
      this.toastService.showError(this.translate.instant('USER_PROFILE.POSTER.INVALID'));
      return;
    }

    const reader = new FileReader();

    reader.onload = () => {
      const result = reader.result!.toString();
      this.posterBase64 = result.split(',')[1] ?? result;
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
    this.toastService.showError(this.translate.instant('USER_PROFILE.FORM.INVALID'));
  }

  handleEventSubmit(): void {
    if (this.userProfileGroup.invalid) {
      return;
    }

    const form = this.userProfileGroup.getRawValue();

    const req = {
      firstName: form.firstName,
      lastName: form.lastName,
      email: form.email,
      location: form.location,
      role: form.role,
      imageBase64: this.posterBase64,
    };
    const user = this.authService.currentUser();

    if (!user) {
      this.toastService.showError('User is not authenticated');
      return;
    }

    const userId = user?.id;

    this.userService
      .updateUserProfile(userId, req)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.isLoading.set(false);
          this.toastService.showSuccess(this.translate.instant('USER_PROFILE.FORM.SUCCESS'));
          this.router.navigate(['/home']);
        },
        error: () => {
          this.isLoading.set(false);
          this.toastService.showError(this.translate.instant('USER_PROFILE.FORM.ERROR'));
        },
      });
  }
}
