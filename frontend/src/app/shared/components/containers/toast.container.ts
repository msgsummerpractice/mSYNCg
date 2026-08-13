import { Component, inject } from '@angular/core';
import { ToastView } from '../views/toast/toast.view';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [ToastView],
  template: `<app-toast></app-toast>`,
})
export class ToastContainer {
  protected readonly toastService = inject(ToastService);

  closeToast(id: string): void {
    this.toastService.removeToast(id);
  }
}
