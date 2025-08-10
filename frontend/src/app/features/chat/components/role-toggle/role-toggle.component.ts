import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-role-toggle',
  templateUrl: './role-toggle.component.html',
  styleUrls: ['./role-toggle.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RoleToggleComponent {
  @Input() selectedRole: 'Creative' | 'Precise' = 'Precise';
  @Output() roleSelected = new EventEmitter<'Creative' | 'Precise'>();

  onRoleChange(role: 'Creative' | 'Precise'): void {
    this.roleSelected.emit(role);
  }
}
