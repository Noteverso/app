export interface BaseProject {
  name: string;
  noteCount: number;
  isFavorite: 0 | 1;
  color: string;
}

export interface FullProject extends BaseProject {
  projectId: string;
  inboxProject: boolean;
}

export type NewProject = BaseProject

export type ProjectOutletContext = {
  projects: FullProject[];
  inboxProject: FullProject;
}

