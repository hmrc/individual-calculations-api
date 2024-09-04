#! python3

# This script checks BSAS for the latest shared code, and copies any shared changes to this API.
#
# To run:
#   ./update_shared.py
# Or if the shebang on the top line doesn't work for you, then:
#   python update_shared.py
#
# Requirements:
#  * Python 3 - make sure you're using at least 3.12.3
#  * pygit2 library - install via your choice of package manager e.g.
#       - pip install pygit2
#       - apt install python3-pygit2
#       - brew install pygit2
#

import os
import pygit2
import shutil
import subprocess

this_filename = os.path.basename(__file__)
this_script_has_uncommitted_changes = False
bsas_api_dir = '../self-assessment-bsas-api'
target_api_dir = '.'
api_name = os.path.basename(os.getcwd())
shared_folders = ['app/shared', 'it/shared', 'test/shared']


def main():
    if is_this_bsas(api_name):
        print("This script updates from BSAS; can't run it on BSAS itself.")
        return

    if not is_bsas_on_main():
        print("BSAS isn't on the main branch. Switch it to main then try again...")
        return

    if has_local_differences(bsas_api_dir):
        print('BSAS has uncommitted changes. Stash or commit them, then try again...')
        return

    if has_local_differences(target_api_dir):
        print(f'{api_name} has uncommitted changes. Stash or commit them, then try again...')
        return

    update_project_from_git(bsas_api_dir, "BSAS")
    update_project_from_git(target_api_dir, "this API")

    if maybe_update_script_from_bsas():
        print(f"I've updated this script file from BSAS; please rerun it.")
        return

    for folder in shared_folders:
        bsas_folder = os.path.join(bsas_api_dir, folder)
        target_folder = os.path.join(target_api_dir, folder)

        shutil.rmtree(target_folder)
        shutil.copytree(bsas_folder, target_folder)

    if has_local_differences(target_api_dir):
        print("""
Done:    The BSAS shared code has differences which I've copied to here.
         Use 'git status' to see which files have changed.

         Next steps:
           1. Run 'coverage' AKA:
                sbt clean coverage test it:test coverageReport
           2. If all good, create and merge a PR with just this shared update and the commit message:
                MTDSA-????? Shared code update
           3. Carry on!
        """)
    elif is_script_modified(target_api_dir):
        print(f"""
The shared code is up-to-date, but {this_filename} has local changes.

Next steps:
  1. Create and merge a PR with just this change
  2. Carry on!
        """)
    else:
        print("Shared is up-to-date.")


def is_this_bsas(api_name):
    return api_name == "self-assessment-bsas-api"


def update_project_from_git(project_dir, project_name):
    print(f"Pulling latest from {project_name}:")
    subprocess.run(f"cd {project_dir}; git pull", shell=True, check=True)
    print()


def has_local_differences(project_dir):
    repo = pygit2.Repository(project_dir)
    status = repo.status()
    num_changed_files = len(status)

    if num_changed_files == 1 and list(status)[0] == this_filename:
        if project_dir == target_api_dir:
            global this_script_has_uncommitted_changes
            this_script_has_uncommitted_changes = True
        return False
    elif num_changed_files > 0:
        return True
    else:
        return False


def is_bsas_on_main():
    repo = pygit2.Repository(bsas_api_dir)
    branch = repo.head.shorthand

    return branch == "main"


def is_script_modified(project_dir):
    from pygit2.enums import FileStatus

    repo = pygit2.Repository(project_dir)
    status = repo.status_file(this_filename)

    return status != FileStatus.CURRENT and status != FileStatus.WT_NEW


def maybe_update_script_from_bsas():
    """Checks if the BSAS script is different, and if so then replaces this one and exits."""

    bsas_file = os.path.join(bsas_api_dir, this_filename)
    target_file = os.path.join(target_api_dir, this_filename)

    with open(bsas_file, "r") as f1, open(target_file, "r") as f2:
        if f1.read() == f2.read():
            return False
        else:
            if this_script_has_uncommitted_changes:
                print(f"""
Warning: This {this_filename} is different from the one in BSAS.
         However, this one has uncommitted changes, so I'll
         leave it alone in case you're working on it.
                """)
                return False
            else:
                print("  copying script from bsas...")
                shutil.copy(bsas_file, target_file)
                return True


if __name__ == '__main__':
    main()
