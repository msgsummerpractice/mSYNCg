import { Component, inject, Input } from '@angular/core';
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
  @Input({ required: true }) toasts!: ReturnType<typeof Array>;
  @Input() onClose!: (id: string) => void;

  getToastClasses(ToastType: 'success' | 'error'): string {
    if (ToastType === 'success') {
      return 'bg-green-50 border border-green-500 text-green-700';
    }
    return 'bg-red-50 border border-red-500 text-red-700';
  }
}
