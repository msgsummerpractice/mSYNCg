import { Injectable, signal } from '@angular/core';

export interface Toast {
  id: string;
  message: string;
  type: 'success' | 'error';
  duration?: number;
}

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  toasts = signal<Toast[]>([]);
  private toastIdCounter = 0;

  showSuccess(message: string, duration: number = 5000): void {
    this.addToast(message, 'success', duration);
  }

  showError(message: string, duration: number = 5000): void {
    this.addToast(message, 'error', duration);
  }

  private addToast(message: string, type: 'success' | 'error', duration: number): void {
    const id = `toast-${++this.toastIdCounter}`;
    const toast: Toast = { id, message, type, duration };

    this.toasts.update((currentToasts) => [...currentToasts, toast]);

    if (duration > 0) {
      setTimeout(() => {
        this.removeToast(id);
      }, duration);
    }
  }

  removeToast(id: string): void {
    this.toasts.update((currentToasts) => currentToasts.filter((t) => t.id !== id));
  }
}