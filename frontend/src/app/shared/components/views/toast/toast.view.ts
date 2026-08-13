import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.view.html',
  styles: `
    @keyframes slideIn {
      from {
        transform: translateX(400px);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .animate-slideIn {
      animation: slideIn 0.3s ease-out;
    }
  `,
})
export class ToastView {
  private readonly toastService = inject(ToastService);
  toasts = this.toastService.toasts;

  getToastClasses(type: 'success' | 'error'): string {
    if (type === 'success') {
      return 'bg-green-50 border border-green-500 text-green-700';
    }
    return 'bg-red-50 border border-red-500 text-red-700';
  }

  closeToast(id: string): void {
    this.toastService.removeToast(id);
  }
}
