import { Component } from '@angular/core';
import { ToastView } from '../views/toast/toast.view';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [ToastView],
  template: `<app-toast></app-toast>`,
})
export class ToastContainer {}