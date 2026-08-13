import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Toast, ToastService } from '../../../../core/services/toast.service';

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
  @Input({ required: true }) toasts!: Toast[];
  @Output() closeToast = new EventEmitter<string>();

  getToastClasses(toastType: Toast['type']): string {
    if (toastType === 'success') {
      return 'bg-green-50 border border-green-500 text-green-700';
    }
    return 'bg-red-50 border border-red-500 text-red-700';
  }

  onCloseClick(id: string): void {
    this.closeToast.emit(id);
  }

}
