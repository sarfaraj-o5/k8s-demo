install-hooks:
	@mkdir -p .git/hooks
	cp -a hooks/* .git/hooks/
	chmod +x .git/hooks/*
	@echo "Hooks installed to .git/hooks"

## make install-hooks ## installs and chmod +x
