import { Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Button } from '../../containers/button.container';
import { UserIconView } from '../../../../features/user/components/views/user-icon.view';

@Component({
  selector: 'app-toolbar-view',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule,Button, UserIconView],
  templateUrl: '../../views/toolbar/toolbar.view.html',
  styleUrl: '../../views/toolbar/toolbar.view.css',
})
export class ToolbarView {}