import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { TranslatePipe } from '@ngx-translate/core';

import { MatDividerModule } from '@angular/material/divider';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatDividerModule, TranslatePipe],
  templateUrl: './home-page.html',
  styleUrl: './home-page.css',
})
export class HomePageComponent {}
